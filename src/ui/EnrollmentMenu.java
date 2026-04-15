package ui;

import domain.Enrollment;
import domain.PaymentType;
import domain.Plan;
import domain.Student;
import application.FitManager;
import application.OperationResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class EnrollmentMenu {

    private final UserInterface ui;
    private final FitManager fitManager;

    public EnrollmentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    // Método principal do menu de matriculas
    public void start() {

        String option;

        do {
            ui.showMenu(
                    "GERENCIAR MATRÍCULAS",
                    """
                            1 - Realizar matrícula
                            2 - Registrar pagamento
                            3 - Cancelar matrícula
                            4 - Consultar matrícula ativa
                            5 - Listar histórico
                            6 - Voltar
                            """
            );

            option = ui.getInput("");

            switch (option) {
                case "1" -> enroll();               // Realizar matrícula

                case "2" -> registerPayment();       // Registrar pagamento

                case "3" -> cancel();                // Cancelar matrícula

                case "4" -> findActiveByStudent();   // Consultar matrícula ativa por CPF

                case "5" -> listAll();               // Listar histórico de matrículas

                case "6" -> ui.showMessage("Voltando ao menu principal...");

                default -> ui.showError("Opção inválida!");
            }

        } while (!option.equals("6"));
    }


    // ================= REALIZAR MATRÍCULA =================
    private void enroll() {

        // Solicita o CPF do aluno
        String cpf = ui.getInput("CPF do aluno");

        // Busca o aluno pelo CPF
        var studentResult = fitManager.findStudentByCpf(cpf);

        // Verifica se a busca foi bem-sucedida
        if (!studentResult.isSuccess()) {
            ui.showError(studentResult.getMessage());
            return;
        }

        // Recupera o objeto Student do resultado
        Student student = (Student) studentResult.getData();

        // Solicita o nome do plano
        String planName = ui.getInput("Nome do plano");

        // Busca o plano pelo nome
        var planResult = fitManager.findPlanByName(planName);

        // Verifica se o plano foi encontrado
        if (!planResult.isSuccess()) {
            ui.showError(planResult.getMessage()); // Exibe erro caso não encontre
            return;
        }

        // Recupera o objeto Plan do resultado
        Plan plan = (Plan) planResult.getData();

        // Solicita a data de inicio da matricula
        String dateInput = ui.getInput("Data de início (dd/MM/yyyy)");

        // Converte a string para LocalDate
        LocalDate startDate = parseDate(dateInput);

        // Valida se a data foi convertida corretamente
        if (startDate == null) {
            ui.showError("Data inválida! Use o formato dd/MM/yyyy.");
            return;
        }

        // Solicita a duração do plano em meses
        String durationInput = ui.getInput("Duração (em meses)");

        // Converte para inteiro
        int durationMonths = parseInt(durationInput);

        // Valida se a duração é válida
        if (durationMonths <= 0) {
            ui.showError("Duração inválida!");
            return;
        }

        // Solicita o valor do pagamento inicial
        String paymentInput = ui.getInput("Valor do pagamento inicial (ex: 99.90)");

        // Converte o pagamento inicial para double
        double initialPayment = parseDouble(paymentInput);

        // Valida se o valor é valido
        if (initialPayment <= 0) {
            ui.showError("Valor inválido!");
            return;
        }

        // Exibe as opções de tipo de pagamento
        ui.showMessage(
                """
                Tipos de pagamento:
                1 - Dinheiro
                2 - Cartão de débito
                3 - Cartão de crédito
                4 - PIX
                """
        );

        // Solicita o tipo de pagamento
        String typeInput = ui.getInput("Escolha o tipo de pagamento");

        // Converte a opção para enum PaymentType
        PaymentType paymentType = parsePaymentType(typeInput);

        // Valida se o tipo de pagamento é válido
        if (paymentType == null) {
            ui.showError("Tipo de pagamento inválido!");
            return;
        }

        // Realiza a matrícula com os dados coletados
        var result = fitManager.enroll(student, plan, startDate, durationMonths, initialPayment, paymentType
        );

        // Verifica se a matrícula foi realizada com sucesso
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }
    // ================= CONSULTAR MATRÍCULA ATIVA =================
    private void findActiveByStudent() {
        // Solicita o CPF do aluno
        String cpf = ui.getInput("CPF do aluno");

        // Busca a matricula ativa do aluno pelo CPF
        var result = fitManager.findActiveEnrollmentByStudent(cpf);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= REGISTRAR PAGAMENTO =================
    private void registerPayment() {

        // Solicita o código da matrícula
        String codeInput = ui.getInput("Código da matrícula");

        int code = parseInt(codeInput);

        if (code <= 0) {
            ui.showError("Código inválido!");
            return;
        }

        // Solicita o valor do pagamento
        String amountInput = ui.getInput("Valor do pagamento (ex: 99.90)");
        double amount = parseDouble(amountInput);

        if (amount <= 0) {
            ui.showError("Valor inválido!");
            return;
        }

        // Exibe os tipos de pagamento disponíveis
        ui.showMessage(
                """
                Tipos de pagamento:
                1 - Dinheiro
                2 - Cartão de débito
                3 - Cartão de crédito
                4 - PIX
                """
        );

        String typeInput = ui.getInput("Escolha o tipo de pagamento");
        // Converte para enum PaymentType
        PaymentType paymentType = parsePaymentType(typeInput);

        if (paymentType == null) {
            ui.showError("Tipo de pagamento inválido!");
            return;
        }

        String description = ui.getInput("Descrição do pagamento");

        // Registra o pagamento no sistema
        var result = fitManager.registerPayment(code, amount, paymentType, description);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= CANCELAR MATRÍCULA =================
    private void cancel() {

        // Solicita o codigo da matricula
        String codeInput = ui.getInput("Código da matrícula");
        int code = parseInt(codeInput);

        if (code <= 0) {
            ui.showError("Código inválido!");
            return;
        }
        // Solicita o cancelamento da matricula
        var result = fitManager.cancelEnrollment(code);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= LISTAR HISTÓRICO =================
    private void listAll() {
        ArrayList<Enrollment> enrollments = fitManager.listEnrollments();

        if (enrollments.isEmpty()) {
            ui.showError("Nenhuma matrícula cadastrada.");
            return;
        }

        ui.showMessage("===== HISTÓRICO DE MATRÍCULAS =====");
        for (int i = 0; i < enrollments.size(); i++) {
            ui.showMessage("---------- " + (i + 1) + " ----------");
            ui.showMessage(enrollments.get(i).toString());
        }
        ui.showMessage("===================================");
    }

    // ================= PRIVADOS =================

    // Converte string para int
    private int parseInt(String input) {
        if (input == null || input.isBlank()) return -1;
        if (!input.matches("\\d+")) return -1;
        return Integer.parseInt(input);
    }

    // Converte string para double
    private double parseDouble(String input) {
        if (input == null || input.isBlank()) return -1;
        if (!input.matches("\\d+(\\.\\d+)?")) return -1;
        return Double.parseDouble(input);
    }

    // Converte string para LocalDate
    private LocalDate parseDate(String input) {
        if (input == null || input.isBlank()) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(input, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    // Converte string para PaymentType
    private PaymentType parsePaymentType(String input) {
        if (input == null || input.isBlank()) return null;
        if (!input.matches("\\d+")) return null;

        int value = Integer.parseInt(input);
        return PaymentType.fromOption(value);
    }

}

