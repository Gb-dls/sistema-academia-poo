package application;

import domain.*;
import domain.plan.Plan;
import domain.payment.*;
import persistence.EnrollmentRepository;
import formatters.DateFormatter;
import exceptions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    // ================= MATRÍCULA =================

    /*
    @ enroll
    @ Objetivo: Realizar a matrícula de um aluno em um plano instanciando a subclasse de pagamento correta
    @ Retorna: Um OperationResult indicando o sucesso da operação ou lança exceções em caso de falha
    */
    public OperationResult<Enrollment> enroll(Student student, Plan plan, String startDateStr, String durationStr, String initialPaymentStr, int paymentOption, String extra1, String extra2, String extra3) throws ValidationException, BusinessException {

        LocalDate startDate = DateFormatter.parseDate(startDateStr);
        if (startDate == null) {
            throw new InvalidFormatFieldException("Data de Início", "dd/MM/yyyy");
        }

        int durationMonths = parseInt(durationStr);
        if (durationMonths <= 0) {
            throw new InvalidFormatFieldException("Duração", "Número inteiro de meses maior que zero");
        }

        double initialPayment = parseDouble(initialPaymentStr);
        if (initialPayment <= 0) {
            throw new InvalidFormatFieldException("Valor do pagamento", "Valor numérico positivo");
        }

        if (enrollmentRepository.findActiveByStudent(student.getCpf()) != null) {
            throw new StudentWithActiveRegistrationException(student.getCpf());
        }

        if (hasDebt(student.getCpf())) {
            throw new BusinessException("Matrícula recusada! O aluno possui débitos pendentes em contratos anteriores.");
        }

        if (durationMonths < plan.getMinDurationMonths()) {
            throw new BusinessException("A duração escolhida (" + durationMonths + " meses) é menor que o mínimo exigido pelo plano (" + plan.getMinDurationMonths() + " meses).");
        }

        double valuePerMonth = plan.calculateTotalPrice(durationMonths) / durationMonths;

        if (initialPayment < valuePerMonth) {
            throw new BusinessException(String.format("O pagamento inicial mínimo exigido pelo plano é de R$ %.2f.", valuePerMonth));
        }

        // Instanciação polimórfica baseada na escolha do Menu de pagamento
        Payment firstPayment;
        switch (paymentOption) {
            case 1 -> { // Dinheiro
                double received = parseDouble(extra1);
                if (received < initialPayment) {
                    throw new BusinessException("O valor entregue em dinheiro é menor que o valor a ser pago.");
                }
                firstPayment = new CashPayment(initialPayment, received);
            }
            case 2 -> { // Cartão de Débito
                if (extra1.isBlank() || extra2.isBlank()) {
                    throw new RequiredFieldException("Dados do cartão de débito");
                }
                firstPayment = new DebitCardPayment(initialPayment, extra1, extra2);
            }
            case 3 -> { // Cartão de Crédito
                int installments = parseInt(extra3);
                if (extra1.isBlank() || extra2.isBlank() || installments <= 0) {
                    throw new RequiredFieldException("Dados do cartão de crédito ou parcelas");
                }
                firstPayment = new CreditCardPayment(initialPayment, extra1, extra2, installments);
            }
            case 4 -> { // PIX
                if (extra1.isBlank()) {
                    throw new RequiredFieldException("Chave PIX");
                }
                firstPayment = new PixPayment(initialPayment, extra1);
            }
            default -> {
                throw new InvalidPaymentMethodException(String.valueOf(paymentOption));
            }
        }
        // ================= CRIAÇÃO DA MATRICULA =================
        int code = enrollmentRepository.useNextCode();
        Enrollment newEnrollment = new Enrollment(code, student, plan, startDate, durationMonths);
        newEnrollment.registerPayment(firstPayment);
        enrollmentRepository.add(newEnrollment);

        return new OperationResult<>(true, "Matrícula efetivada com sucesso.",newEnrollment);
    }

    // ================= PAGAMENTO AVULSO =================
    /*
    @ registerPayment
    @ Objetivo: Registra um pagamento em uma matrícula existente validando limites e regras de negócio
    @ Retorna: Um OperationResult contendo o status de sucesso e a atualização do saldo devedor
    */
    public OperationResult<Enrollment> registerPayment(String codeStr, String amountStr, int paymentOption, String extra1, String extra2, String extra3) throws ValidationException, BusinessException {

        int enrollmentCode = parseInt(codeStr);
        if (enrollmentCode <= 0) {
            throw new InvalidFormatFieldException("Código de matrícula", "Número inteiro válido");
        }

        double amount = parseDouble(amountStr);
        if (amount <= 0) {
            throw new InvalidFormatFieldException("Valor de pagamento", "Valor numérico positivo");
        }

        Enrollment flagEnrollment = enrollmentRepository.findByCode(enrollmentCode);
        if (flagEnrollment == null) {
            throw new BusinessException("Matrícula de código " + enrollmentCode + " não encontrada no sistema.");
        }

        if (flagEnrollment.getStatus() == EnrollmentStatus.CANCELLED && flagEnrollment.calculateBalance() <= 0) {
            throw new BusinessException("Não é possível registrar pagamentos. Esta matrícula já está cancelada e quitada.");
        }

        if (amount > flagEnrollment.calculateBalance()) {
            throw new PaymentValueMismatchException(flagEnrollment.calculateBalance(), amount);
        }

        // Instanciação polimórfica para novos pagamentos avulsos
        Payment newPayment;
        switch (paymentOption) {
            case 1 -> {
                double received = parseDouble(extra1);
                if (received < amount) {
                    throw new BusinessException("O valor entregue em dinheiro é insuficiente.");
                }
                newPayment = new CashPayment(amount, received);
            }
            case 2 -> {
                if (extra1.isBlank() || extra2.isBlank()) throw new RequiredFieldException("Dados do cartão de débito");
                newPayment = new DebitCardPayment(amount, extra1, extra2);
            }
            case 3 -> {
                int installments = parseInt(extra3);
                if (extra1.isBlank() || extra2.isBlank() || installments <= 0) {
                    throw new RequiredFieldException("Dados do cartão de crédito ou parcelas");
                }
                newPayment = new CreditCardPayment(amount, extra1, extra2, installments);
            }
            case 4 -> {
                if (extra1.isBlank()) throw new RequiredFieldException("Chave PIX");
                newPayment = new PixPayment(amount, extra1);
            }
            default -> {
                throw new InvalidPaymentMethodException(String.valueOf(paymentOption));
            }
        }

        flagEnrollment.registerPayment(newPayment);
        double balance = flagEnrollment.calculateBalance();
        String statusFinanceiro = (balance > 0) ? String.format(" Saldo pendente: R$ %.2f", balance) : " Matrícula quitada.";

        String changeMessage = "";
        if (newPayment instanceof CashPayment cash) {
            double troco = cash.getChange();
            if (troco > 0) {
                changeMessage = String.format(" | Troco a devolver: R$ %.2f", troco);
            }
        }

        return new OperationResult<>(true, "Pagamento registrado com sucesso." + statusFinanceiro + changeMessage);
    }

    // ================= CANCELAMENTO =================
    /*
    @ cancel
    @ Objetivo: Solicitar o cancelamento de uma matrícula e retornar o extrato financeiro final recalculado.
    */
    public OperationResult<Void> cancel(String codeStr) throws ValidationException, BusinessException {
        int code = parseInt(codeStr);

        if (code <= 0) throw new InvalidFormatFieldException("Código de matrícula", "Número inteiro válido");

        Enrollment flagEnrollment =  enrollmentRepository.findByCode(code);
        if (flagEnrollment == null) throw new BusinessException("Matrícula não encontrada no sistema.");
        if (flagEnrollment.getStatus() == EnrollmentStatus.CANCELLED) throw new BusinessException("A matrícula informada já está cancelada.");

        // Guarda os valores de antes do cancelamento apenas para o relatório técnico
        double originalContract = flagEnrollment.getTotalPrice();

        // Aqui a matrícula muda o status para cancelado, calcula os meses ativos, aplica a multa e atualiza o totalPrice
        flagEnrollment.cancel();

        // calculateBalance() vai retornar o acerto de contas final (Proporcional + Multa - O que já foi pago)
        double finalDebit = flagEnrollment.calculateBalance();
        double totalPaid = flagEnrollment.calculateTotalPaid();

        String resumo = String.format(
                "Matrícula CANCELADA com sucesso!\n" +
                        "--- RESUMO DE ENCERRAMENTO (AJUSTADO) ---\n" +
                        "Valor do Contrato Original: R$ %.2f\n" +
                        "Novo Valor Total Devido (Proporcional + Multa): R$ %.2f\n" +
                        "Total Pago pelo Aluno Até Hoje: R$ %.2f\n" +
                        "-----------------------------------------\n",
                originalContract, flagEnrollment.getTotalPrice(), totalPaid
        );

        if (finalDebit > 0) {
            resumo += String.format("DÉBITO PENDENTE TOTAL A QUITAR: R$ %.2f\n" +
                    "O aluno deve realizar o pagamento avulso no menu 2 para regularizar a situação.", finalDebit);
        } else {
            resumo += "Contrato encerrado sem pendências financeiras. Situação regularizada.";
        }

        return new OperationResult<>(true, resumo);
    }

    // ================= CONSULTAS =================
    /*
    @ hasActiveEnrollment
    @ Objetivo: Verificar se um determinado aluno possui um contrato com o status ativo no momento
    @ Retorna: Um valor booleano (true se houver matrícula ativa, false caso contrário)
    */
    public boolean hasActiveEnrollment(String cpf) {
        return enrollmentRepository.findActiveByStudent(cpf) != null;
    }


    /*
    @ findActiveByStudent
    @ Objetivo: Localizar o objeto de matrícula que esteja atualmente ativo no sistema utilizando o CPF do aluno
    @ Retorna: O objeto Enrollment ativo correspondente ao aluno ou null se não houver nenhum contrato ativo
    */
    public Enrollment findActiveByStudent(String cpf) {
        String cleanCpf = DateFormatter.cleanNumber(cpf);
        for (Enrollment e : enrollmentRepository.listAll()) {
            if (e.getStudent().getCpf().equals(cleanCpf) && e.getStatus() == EnrollmentStatus.ACTIVE) {
                return e;
            }
        }
        return null;
    }

    /*
    @ listEnrollments
    @ Objetivo: Fornecer uma cópia de segurança da lista global para fins de listagem, preservando a coleção original
    @ Retorna: Um ArrayList contendo todas as matrículas registradas no sistema
    */
    public OperationResult<ArrayList<Enrollment>> listEnrollments() {

        ArrayList<Enrollment> list = enrollmentRepository.listAll();
        if (list.isEmpty()) {
            return new OperationResult<>(false, "Nenhuma matrícula cadastrada.");
        }
        return new OperationResult<>(true, "Lista de matrículas carregada.", list);
    }
    /*
    @ getEnrollmentsByStudent
    @ Objetivo: Filtrar histórico de contratos vinculados ao CPF de um aluno.
    */
    public List<Enrollment> getEnrollmentsByStudent(String cpf) {
        return enrollmentRepository.findAllByStudent(cpf);
    }

    /*
    @ hasDebt
    @ Objetivo: Analisar o histórico de um cliente para identificar se ele possui parcela ou saldo devedor em aberto
    @ Retorna: Um valor booleano indicando se há débitos pendentes.
    */
    public boolean hasDebt(String cpf) {
        List<Enrollment> list = enrollmentRepository.findAllByStudent(cpf);
        for(Enrollment e : list){
            if (e.calculateBalance() > 0) {
                return true;
            }
        }
        return false;
    }

    // ================= PRIVADOS =================
    /*
    @ parseInt
    @ Objetivo: Realizar o tratamento e a conversão segura de dados textuais para números inteiros
    @ Retorna: O valor numérico convertido ou -1 caso o formato do texto seja inválido.
    */
    private int parseInt(String input) {
        if (input == null || input.isBlank() || !input.matches("\\d+")) return -1;
        return Integer.parseInt(input);
    }

    /*
    @ parseDouble
    @ Objetivo: Tratar e converter valores monetários textuais em tipos numéricos flutuantes
    @ Retorna: O valor em double convertido ou -1 se a string não for um número válido
    */
    private double parseDouble(String input) {
        if (input == null || input.isBlank() || !input.matches("\\d+(\\.\\d+)?")) return -1;
        return Double.parseDouble(input);
    }

    // ================= RELATÓRIO FINANCEIRO =================

    /*
    @ generateFinancialReport
    @ Objetivo: Varrer o histórico de matrículas e pagamentos para consolidar métricas de um periodo
    @ Retorna: O objeto FinancialReport preenchido (ou zerado)
    */
    public FinancialReport generateFinancialReport(int month, int year) {
        FinancialReport report = new FinancialReport(month, year);

        for (Enrollment e : enrollmentRepository.listAll()) {

            // Verifica matrículas INICIADAS no período
            LocalDate startDate = e.getStartDate();
            if (startDate.getMonthValue() == month && startDate.getYear() == year) {
                report.incrementStartedEnrollment();
                // Registra qual plano foi contratado para o ranking
                report.recordPlanContraction(e.getPlan().getPlanTypeName());
            }

            // Verifica matrículas CANCELADAS no período
            if (e.getStatus() == EnrollmentStatus.CANCELLED) {
                LocalDate endDate = e.getEndDate();
                if (endDate != null && endDate.getMonthValue() == month && endDate.getYear() == year) {
                    report.incrementCancelledEnrollment();
                }
            }

            // Varre os PAGAMENTOS dessa matrícula e soma as receitas do período
            List<Payment> payments = e.getPayments();
            if (payments != null) {
                for (Payment p : payments) {
                    LocalDate pDate = p.getDate();
                    if (pDate.getMonthValue() == month && pDate.getYear() == year) {
                        report.addRevenue(p.getAmount());
                        report.addProcessingFee(p.getProcessingFee());
                        report.addRevenueByPaymentMethod(p.getPaymentMethodName(), p.getAmount());
                        report.addRevenueByPlanType(e.getPlan().getPlanTypeName(), p.getAmount());
                    }
                }
            }
        }

        return report;
    }

}