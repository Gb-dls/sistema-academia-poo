package application;

import domain.*;
import domain.payment.*; // Importa o subpacote com as novas subclasses
import formatters.DateFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentService {

    private static int nextCode = 1;
    private ArrayList<Enrollment> enrollments;

    public EnrollmentService() {
        this.enrollments = new ArrayList<>();
    }

    /*
    @ enroll
    @ Objetivo: Realizar a matrícula de um aluno em um plano instanciando a subclasse de pagamento correta
    */
    public OperationResult enroll(Student student, Plan plan, String startDateStr, String durationStr, String initialPaymentStr, int paymentOption, String extra1, String extra2, String extra3) {

        LocalDate startDate = DateFormatter.parseDate(startDateStr);
        if (startDate == null) {
            return new OperationResult(false, "Data de início inválida! Use o formato dd/MM/yyyy.");
        }

        int durationMonths = parseInt(durationStr);
        if (durationMonths <= 0) {
            return new OperationResult(false, "Duração inválida!");
        }

        double initialPayment = parseDouble(initialPaymentStr);
        if (initialPayment <= 0) {
            return new OperationResult(false, "Valor de pagamento inválido!");
        }

        if (hasActiveEnrollment(student.getCpf())) {
            return new OperationResult(false, "O aluno já possui uma matrícula ativa no sistema.");
        }

        if (durationMonths < plan.getMinDurationMonths()) {
            return new OperationResult(false, "A duração escolhida é menor que o mínimo exigido pelo plano (" + plan.getMinDurationMonths() + " meses).");
        }

        double valuePerMonth = plan.calculateTotalPrice(durationMonths) / durationMonths;

        if (initialPayment < valuePerMonth) {
            return new OperationResult(false, String.format("O pagamento inicial mínimo exigido é de R$ %.2f.", valuePerMonth));
        }

        // Instanciação polimórfica baseada na escolha do Menu
        Payment firstPayment;
        switch (paymentOption) {
            case 1 -> { // Dinheiro
                double received = parseDouble(extra1);
                if (received < initialPayment) {
                    return new OperationResult(false, "Erro: O valor entregue em dinheiro é menor que o valor a ser pago.");
                }
                firstPayment = new CashPayment(initialPayment, received);
            }
            case 2 -> { // Cartão de Débito
                if (extra1.isBlank() || extra2.isBlank()) return new OperationResult(false, "Dados do cartão de débito são obrigatórios.");
                firstPayment = new DebitCardPayment(initialPayment, extra1, extra2);
            }
            case 3 -> { // Cartão de Crédito
                int installments = parseInt(extra3);
                if (extra1.isBlank() || extra2.isBlank() || installments <= 0) {
                    return new OperationResult(false, "Dados do cartão de crédito ou parcelas inválidos.");
                }
                firstPayment = new CreditCardPayment(initialPayment, extra1, extra2, installments);
            }
            case 4 -> { // PIX
                if (extra1.isBlank()) return new OperationResult(false, "A chave PIX é obrigatória.");
                firstPayment = new PixPayment(initialPayment, extra1);
            }
            default -> {
                return new OperationResult(false, "Opção de pagamento inválida no sistema.");
            }
        }

        Enrollment newEnrollment = new Enrollment(nextCode, student, plan, startDate, durationMonths);
        newEnrollment.registerPayment(firstPayment);
        enrollments.add(newEnrollment);
        nextCode++;

        return new OperationResult(true, "Matrícula efetivada com sucesso!");
    }

    /*
    @ registerPayment
    @ Objetivo: Registra um pagamento polimórfico em uma matrícula existente validando limites e regras de negócio
    */
    public OperationResult registerPayment(String codeStr, String amountStr, int paymentOption, String extra1, String extra2, String extra3) {

        int enrollmentCode = parseInt(codeStr);
        if (enrollmentCode <= 0) {
            return new OperationResult(false, "Código de matrícula inválido!");
        }

        double amount = parseDouble(amountStr);
        if (amount <= 0) {
            return new OperationResult(false, "O valor do pagamento deve ser maior que zero.");
        }

        Enrollment flagEnrollment = findByCode(enrollmentCode);
        if (flagEnrollment == null) {
            return new OperationResult(false, "Matrícula de código " + enrollmentCode + " não encontrada no sistema.");
        }

        if (flagEnrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            return new OperationResult(false, "Não é possível registrar pagamentos em uma matrícula cancelada.");
        }

        if (amount > flagEnrollment.calculateBalance()) {
            return new OperationResult(false, "O valor pago supera o saldo devedor. Pagamento máximo permitido: R$ " + flagEnrollment.calculateBalance());
        }

        // Instanciação polimórfica para novos pagamentos avulsos
        Payment newPayment;
        switch (paymentOption) {
            case 1 -> {
                double received = parseDouble(extra1);
                if (received < amount) {
                    return new OperationResult(false, "Erro: O valor entregue em dinheiro é insuficiente.");
                }
                newPayment = new CashPayment(amount, received);
            }
            case 2 -> {
                if (extra1.isBlank() || extra2.isBlank()) return new OperationResult(false, "Dados do cartão de débito são obrigatórios.");
                newPayment = new DebitCardPayment(amount, extra1, extra2);
            }
            case 3 -> {
                int installments = parseInt(extra3);
                if (extra1.isBlank() || extra2.isBlank() || installments <= 0) {
                    return new OperationResult(false, "Dados do cartão de crédito ou parcelas inválidos.");
                }
                newPayment = new CreditCardPayment(amount, extra1, extra2, installments);
            }
            case 4 -> {
                if (extra1.isBlank()) return new OperationResult(false, "A chave PIX é obrigatória.");
                newPayment = new PixPayment(amount, extra1);
            }
            default -> {
                return new OperationResult(false, "Opção de pagamento inválida.");
            }
        }

        flagEnrollment.registerPayment(newPayment);
        double balance = flagEnrollment.calculateBalance();
        String statusFinanceiro = (balance > 0) ? String.format(" Saldo pendente: R$ %.2f", balance) : " Matrícula quitada!";

        return new OperationResult(true, "Pagamento registrado com sucesso!" + statusFinanceiro);
    }

    public OperationResult cancel(String codeStr) {
        int code = parseInt(codeStr);
        if (code <= 0) return new OperationResult(false, "Código de matrícula inválido!");
        Enrollment flagEnrollment = findByCode(code);
        if (flagEnrollment == null) return new OperationResult(false, "Matrícula não encontrada no sistema.");
        if (flagEnrollment.getStatus() == EnrollmentStatus.CANCELLED) return new OperationResult(false, "A matrícula informada já está cancelada.");

        flagEnrollment.cancel();
        double totalContract = flagEnrollment.getTotalPrice();
        double totalPaid = flagEnrollment.calculateTotalPaid();
        double balance = flagEnrollment.calculateBalance();

        String resumo = String.format("Matrícula cancelada com sucesso!\n--- RESUMO FINANCEIRO ---\nValor Total do Contrato: R$ %.2f\nTotal Já Pago: R$ %.2f\n", totalContract, totalPaid);
        resumo += (balance > 0) ? String.format("Valor Pendente: R$ %.2f", balance) : "O valor foi pago corretamente.";

        return new OperationResult(true, resumo);
    }

    public Enrollment findByCode(int code) {
        for (Enrollment e : enrollments) {
            if (e.getCode() == code) return e;
        }
        return null;
    }

    public boolean hasActiveEnrollment(String cpf) {
        return findActiveByStudent(cpf) != null;
    }

    public Enrollment findActiveByStudent(String cpf) {
        String cleanCpf = DateFormatter.cleanNumber(cpf);
        for (Enrollment e : enrollments) {
            if (e.getStudent().getCpf().equals(cleanCpf) && e.getStatus() == EnrollmentStatus.ACTIVE) {
                return e;
            }
        }
        return null;
    }

    public ArrayList<Enrollment> listEnrollments() {
        return new ArrayList<>(this.enrollments);
    }

    public List<Enrollment> getEnrollmentsByStudent(String cpf) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : enrollments) {
            if (e.getStudent().getCpf().equals(cpf)) result.add(e);
        }
        return result;
    }

    public boolean hasDebt(String cpf) {
        List<Enrollment> studentEnrollments = getEnrollmentsByStudent(cpf);
        for (Enrollment e : studentEnrollments) {
            if (e.calculateBalance() > 0) return true;
        }
        return false;
    }

    private int parseInt(String input) {
        if (input == null || input.isBlank() || !input.matches("\\d+")) return -1;
        return Integer.parseInt(input);
    }

    private double parseDouble(String input) {
        if (input == null || input.isBlank() || !input.matches("\\d+(\\.\\d+)?")) return -1;
        return Double.parseDouble(input);
    }
}