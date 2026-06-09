package application;

import domain.*;
import domain.plan.Plan;
import domain.payment.*;
import formatters.DateFormatter;
import exceptions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentService extends Repository<Enrollment>  {

    private static int nextCode = 1;


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

        if (hasActiveEnrollment(student.getCpf())) {
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

        // Instanciação polimórfica baseada na escolha do Menu
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

        Enrollment newEnrollment = new Enrollment(nextCode, student, plan, startDate, durationMonths);
        newEnrollment.registerPayment(firstPayment);
        items.add(newEnrollment);
        nextCode++;

        return new OperationResult<>(true, "Matrícula efetivada com sucesso.");
    }

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

        Enrollment flagEnrollment = findByCode(enrollmentCode);
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

    /*
    @ cancel
    @ Objetivo: Solicitar o cancelamento de uma matrícula e retornar o extrato financeiro final recalculado.
    */
    public OperationResult<Void> cancel(String codeStr) throws ValidationException, BusinessException {
        int code = parseInt(codeStr);

        if (code <= 0) throw new InvalidFormatFieldException("Código de matrícula", "Número inteiro válido");

        Enrollment flagEnrollment = findByCode(code);
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

    /*
    @ findByCode
    @ Objetivo: Buscar e retornar uma matrícula específica dentro do histórico geral utilizando o código identificador
    @ Retorna: O objeto Enrollment correspondente ao código informado ou null caso não seja localizado
    */
    public Enrollment findByCode(int code) {
        for (Enrollment e : items) {
            if (e.getCode() == code) return e;
        }
        return null;
    }

    /*
    @ hasActiveEnrollment
    @ Objetivo: Verificar se um determinado aluno possui um contrato com o status ativo no momento
    @ Retorna: Um valor booleano (true se houver matrícula ativa, false caso contrário)
    */
    public boolean hasActiveEnrollment(String cpf) {
        return findActiveByStudent(cpf) != null;
    }

    /*
    @ findActiveByStudent
    @ Objetivo: Localizar o objeto de matrícula que esteja atualmente ativo no sistema utilizando o CPF do aluno
    @ Retorna: O objeto Enrollment ativo correspondente ao aluno ou null se não houver nenhum contrato ativo
    */
    public Enrollment findActiveByStudent(String cpf) {
        String cleanCpf = DateFormatter.cleanNumber(cpf);
        for (Enrollment e : items) {
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
        if(count() == 0){
            return new OperationResult<>(false, "Nenhuma matrícula cadastrada.");
        }
        return new OperationResult<>(true, "Lista de matrículas carregada.", listAll()
        );
    }

    /*
    @ getEnrollmentsByStudent
    @ Objetivo: Filtrar histórico de contratos vinculados ao CPF de um aluno.
    */
    public List<Enrollment> getEnrollmentsByStudent(String cpf) {
        List<Enrollment> result = new ArrayList<>();
        // Remove pontos e traços para garantir que a comparação seja apenas dos números
        String cleanCpf = cpf.replaceAll("\\D", "");

        for (Enrollment e : items) {
            String studentCleanCpf = e.getStudent().getCpf().replaceAll("\\D", "");
            if (studentCleanCpf.equals(cleanCpf)) {
                result.add(e);
            }
        }
        return result;
    }

    /*
    @ hasDebt
    @ Objetivo: Analisar o histórico de um cliente para identificar se ele possui parcela ou saldo devedor em aberto
    @ Retorna: Um valor booleano indicando se há débitos pendentes.
    */
    public boolean hasDebt(String cpf) {
        List<Enrollment> studentEnrollments = getEnrollmentsByStudent(cpf);
        for (Enrollment e : studentEnrollments) {
            if (e.calculateBalance() > 0) return true;
        }
        return false;
    }

    /*
    @ parseInt
    @ Objetivo: Realizar o tratamento e a conversão segura de dados textuais para números inteiros
    @ Retorna: O valor numérico convertido ou -1 caso o formato do texto seja inválido.
    */
    private int parseInt(String input) {
        if (input == null || input.isBlank() || !input.matches("\\d+")) return -1;
        return Integer.parseInt(input);
    }
    // metodos concretos de repository implementar quando for inserir os arquivos
    @Override
    public void save(String filePath) {

    }

    @Override
    public void load(String filePath) {

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


}