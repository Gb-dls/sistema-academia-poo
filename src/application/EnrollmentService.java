package application;

import domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Classe responsável pela lógica de negócio das matrículas
// Aqui ficam regras como: matrícula, pagamento, cancelamento e consultas
public class EnrollmentService {

    // ================= ATRIBUTOS =================

    // Gerador automático de código de matrícula (incremental)
    private static int nextCode = 1;

    // Lista que armazena todas as matrículas em memória
    private ArrayList<Enrollment> enrollments;

    // ================= CONSTRUTOR =================

    public EnrollmentService() {
        this.enrollments = new ArrayList<>();   // Inicializando o ArrayList
    }



    // Realiza a matrícula de um aluno em um plano
    public OperationResult enroll(Student student, Plan plan, String startDateStr, String durationStr, String initialPaymentStr, PaymentType paymentType) {

        // Converte e valida data de início
        LocalDate startDate = parseDate(startDateStr);
        if (startDate == null) {
            return new OperationResult(false, "Data de início inválida! Use o formato dd/MM/yyyy.");
        }

        // Converte e valida duração do plano
        int durationMonths = parseInt(durationStr);
        if (durationMonths <= 0) {
            return new OperationResult(false, "Duração inválida!");
        }

        // Converte e valida pagamento inicial
        double initialPayment = parseDouble(initialPaymentStr);
        if (initialPayment <= 0) {
            return new OperationResult(false, "Valor de pagamento inválido!");
        }

        // Verifica se o aluno já possui matrícula ativa
        if (hasActiveEnrollment(student.getCpf())) {
            return new OperationResult(false, "O aluno já possui uma matrícula ativa no sistema.");
        }

        // Valida duração mínima exigida pelo plano
        if (durationMonths < plan.getMinDurationMonths()) {
            return new OperationResult(false, "A duração escolhida é menor que o mínimo exigido pelo plano (" + plan.getMinDurationMonths() + " meses).");
        }

        // Calcula valor mensal do contrato
        double valuePerMonth = plan.calculateTotalPrice(durationMonths) / durationMonths;

        // Valida pagamento inicial mínimo
        if (initialPayment < valuePerMonth) {
            return new OperationResult(false, String.format("O pagamento inicial mínimo exigido é de R$ %.2f.", valuePerMonth));
        }

        // Criação da matrícula
        Enrollment newEnrollment = new Enrollment(nextCode, student, plan, startDate, durationMonths);

        // Registra primeiro pagamento
        Payment firstPayment = new Payment(initialPayment, paymentType, "Pagamento da 1ª Mensalidade");
        newEnrollment.registerPayment(firstPayment);

        // Adiciona matrícula na lista
        enrollments.add(newEnrollment);

        // Incrementa código automático
        nextCode++;

        return new OperationResult(true, "Matrícula efetivada com sucesso!");
    }


    // ================= PAGAMENTO =================

    // Registra um pagamento em uma matrícula existente
    public OperationResult registerPayment(String codeStr, String amountStr, PaymentType type, String description) {

        // Converte e valida código da matrícula
        int enrollmentCode = parseInt(codeStr);
        if (enrollmentCode <= 0) {
            return new OperationResult(false, "Código de matrícula inválido!");
        }

        // Converte e valida valor do pagamento
        double amount = parseDouble(amountStr);
        if (amount <= 0) {
            return new OperationResult(false, "O valor do pagamento deve ser maior que zero.");
        }

        // Busca matrícula pelo código
        Enrollment flagEnrollment = findByCode(enrollmentCode);
        if (flagEnrollment == null) {
            return new OperationResult(false, "Matrícula de código " + enrollmentCode + " não encontrada no sistema.");
        }

        // Impede pagamento em matrícula cancelada
        if (flagEnrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            return new OperationResult(false, "Não é possível registrar pagamentos em uma matrícula cancelada.");
        }

        // Evita pagamento maior que a dívida
        if (amount > flagEnrollment.calculateBalance()) {
            return new OperationResult(false, "O valor pago supera o saldo devedor. Pagamento máximo permitido: R$ " + flagEnrollment.calculateBalance());
        }

        // Cria novo pagamento
        Payment newPayment = new Payment(amount, type, description);

        // Registra pagamento na matrícula
        flagEnrollment.registerPayment(newPayment);

        // Calcula saldo atualizado
        double balance = flagEnrollment.calculateBalance();

        String statusFinanceiro;

        if (balance > 0) {
            statusFinanceiro = String.format(" Saldo pendente: R$ %.2f", balance);
        } else {
            statusFinanceiro = " Matrícula quitada!";
        }

        return new OperationResult(true, "Pagamento registrado com sucesso!" + statusFinanceiro);
    }

    // ================= CANCELAMENTO =================

    // Cancela uma matrícula e gera resumo financeiro
    public OperationResult cancel(String codeStr) {

        // Converte código
        int code = parseInt(codeStr);

        // Valida se o código é válido (não pode ser <= 0)
        if (code <= 0) {
            return new OperationResult(false, "Código de matrícula inválido!");
        }

        // Busca a matrícula pelo código informado
        Enrollment flagEnrollment = findByCode(code);

        // Verifica se a matrícula existe no sistema
        if (flagEnrollment == null) {
            return new OperationResult(false, "Matrícula não encontrada no sistema.");
        }

        // Verifica se a matrícula já está cancelada
        if (flagEnrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            return new OperationResult(false, "A matrícula informada já está cancelada.");
        }

        // Realiza o cancelamento da matrícula (muda o status internamente)
        flagEnrollment.cancel();

        // Obtém o valor total do contrato
        double totalContract = flagEnrollment.getTotalPrice();

        // Calcula quanto já foi pago pelo aluno
        double totalPaid = flagEnrollment.calculateTotalPaid();

        // Calcula o saldo restante (quanto ainda falta pagar)
        double balance = flagEnrollment.calculateBalance();

        // Monta a mensagem inicial do resumo financeiro
        String resumo = String.format(
                "Matrícula cancelada com sucesso!\n" +
                        "--- RESUMO FINANCEIRO ---\n" +
                        "Valor Total do Contrato: R$ %.2f\n" +
                        "Total Já Pago: R$ %.2f\n",
                totalContract, totalPaid
        );

        // Verifica se ainda existe valor pendente
        if (balance > 0) {
            // Se ainda deve, mostra o valor que falta pagar
            resumo += String.format("Valor Pendente: R$ %.2f", balance);
        } else {
            // Se não há dívida, informa que está tudo pago
            resumo += "O valor foi pago corretamente.";
        }

        // Retorna o resultado da operação com sucesso + resumo financeiro
        return new OperationResult(true, resumo);
    }

    // ================= CONSULTAS =================
    // Metodo de busca por codigo se achar devolve a matricula senao devolve null
    public Enrollment findByCode(int code) {
        for (Enrollment e : enrollments) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }

    // Metodo que verifica se uma matricula esta ativa de acordo com o cpf do student (true ativa - false cancelada)
    public boolean hasActiveEnrollment(String cpf) {

        // Busca para achar a matricula
        Enrollment matriculaEncontrada = findActiveByStudent(cpf);

        // Checando se encontrou
        if (matriculaEncontrada != null) {
            return true;
        } else {
            return false;
        }
    }

    // Metodo que devolve a matricula por uma busca por cpf do aluno
    public Enrollment findActiveByStudent(String cpf) {

        String cleanCpf = cleanNumber(cpf);

        for (Enrollment e : enrollments) {

            if (e.getStudent().getCpf().equals(cleanCpf)
                    && e.getStatus() == EnrollmentStatus.ACTIVE) {
                return e;
            }
        }
        return null;
    }

    // Metodo que devolve uma copia da lista enrollments
    public ArrayList<Enrollment> listEnrollments() {
        return new ArrayList<>(this.enrollments);
    }

    // Retorna todas as matrículas de um aluno pelo CPF
    public List<Enrollment> getEnrollmentsByStudent(String cpf) {
        List<Enrollment> result = new ArrayList<>();
        for (int i = 0; i < enrollments.size(); i++) {
            if (enrollments.get(i).getStudent().getCpf().equals(cpf)) {
                result.add(enrollments.get(i));
            }
        }
        return result;
    }

    // Verifica se o aluno possui algum débito pendente
    public boolean hasDebt(String cpf) {

        // Busca todas as matrículas do aluno pelo CPF
        List<Enrollment> studentEnrollments = getEnrollmentsByStudent(cpf);

        // Percorre todas as matrículas encontradas
        for (int i = 0; i < studentEnrollments.size(); i++) {

            // Chama o calculateBalance() de cada matrícula e verifica se o aluno ainda tem valor a pagar
            if (studentEnrollments.get(i).calculateBalance() > 0) {
                return true;
            }
        }
        return false;
    }
// ================= PRIVADOS =================


    // Converte String para int com validação
    private int parseInt(String input) {
        if (input == null || input.isBlank()) return -1;
        if (!input.matches("\\d+")) return -1;
        return Integer.parseInt(input);
    }

    // Converte String para double com validação
    private double parseDouble(String input) {
        if (input == null || input.isBlank()) return -1;
        if (!input.matches("\\d+(\\.\\d+)?")) return -1;
        return Double.parseDouble(input);
    }

    // Converte String para LocalDate (dd/MM/yyyy)
    private LocalDate parseDate(String input) {
        if (input == null || input.isBlank()) return null;
        if (!input.matches("\\d{2}/\\d{2}/\\d{4}")) return null;

        int day   = Integer.parseInt(input.substring(0, 2));
        int month = Integer.parseInt(input.substring(3, 5));
        int year  = Integer.parseInt(input.substring(6, 10));

        if (month < 1 || month > 12) return null;
        if (day < 1 || day > 31)     return null;

        return LocalDate.of(year, month, day);
    }

    // Remove caracteres não numéricos
    private String cleanNumber(String input) {
        if (input == null) return "";
        return input.replaceAll("\\D", "");
    }


}
