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


// Classe responsável pelo menu de MATRÍCULAS
// Faz a interface entre o usuário e o FitManager
public class EnrollmentMenu {

    // Interface de comunicação com o usuário (entrada/saída)
    private final UserInterface ui;

    // Fachada do sistema (encaminha para os services)
    private final FitManager fitManager;

    // Construtor das dependências necessárias
    public EnrollmentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    // ================= MENU PRINCIPAL =================
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
        // Solicita CPF do aluno
        String cpf = ui.getInput("CPF do aluno");

        // Busca aluno no sistema
        var studentResult = fitManager.findStudentByCpf(cpf);

        if (!studentResult.isSuccess()) {
            ui.showError(studentResult.getMessage());
            return;
        }

        // Converte retorno para Student
        Student student = (Student) studentResult.getData();

        // Solicita plano
        String planName = ui.getInput("Nome do plano");

        // Busca plano no sistema
        var planResult = fitManager.findPlanByName(planName);
        if (!planResult.isSuccess()) {
            ui.showError(planResult.getMessage());
            return;
        }

        // Converte retorno para Plan
        Plan plan = (Plan) planResult.getData();

        // Coleta dados da matrícula
        String dateInput     = ui.getInput("Data de início (dd/MM/yyyy)");
        String durationInput = ui.getInput("Duração (em meses)");
        String paymentInput  = ui.getInput("Valor do pagamento inicial (ex: 99.90)");

        // Exibe opções de pagamento
        ui.showMessage("""
            Tipos de pagamento:
            1 - Dinheiro
            2 - Cartão de débito
            3 - Cartão de crédito
            4 - PIX
            """);

        String typeInput = ui.getInput("Escolha o tipo de pagamento");

        // Converte opção para enum PaymentType
        PaymentType paymentType = parsePaymentType(typeInput);

        if (paymentType == null) {
            ui.showError("Tipo de pagamento inválido!");
            return;
        }

        // Executa matrícula via FitManager
        var result = fitManager.enroll(student, plan, dateInput, durationInput, paymentInput, paymentType);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        }
        else {
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

        // Coleta dados do pagamento
        String codeInput   = ui.getInput("Código da matrícula");
        String amountInput = ui.getInput("Valor do pagamento (ex: 99.90)");

        ui.showMessage("""
            Tipos de pagamento:
            1 - Dinheiro
            2 - Cartão de débito
            3 - Cartão de crédito
            4 - PIX
            """);

        String typeInput = ui.getInput("Escolha o tipo de pagamento");

        PaymentType paymentType = parsePaymentType(typeInput);
        if (paymentType == null) {
            ui.showError("Tipo de pagamento inválido!");
            return;
        }

        String description = ui.getInput("Descrição do pagamento");

        // Registra pagamento via FitManager
        var result = fitManager.registerPayment(codeInput, amountInput, paymentType, description);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        }
        else {
            ui.showError(result.getMessage());
        }
    }

    // ================= CANCELAR MATRÍCULA =================

    private void cancel() {

        // Solicita código da matrícula
        String codeInput = ui.getInput("Código da matrícula");

        // Cancela matrícula
        var result = fitManager.cancelEnrollment(codeInput);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        }
        else {
            ui.showError(result.getMessage()); }
    }


    // ================= LISTAR HISTÓRICO =================
    private void listAll() {

        // Busca todas as matrículas
        ArrayList<Enrollment> enrollments = fitManager.listEnrollments();

        if (enrollments.isEmpty()) {
            ui.showError("Nenhuma matrícula cadastrada.");
            return;
        }

        ui.showMessage("===== HISTÓRICO DE MATRÍCULAS =====");
        // Percorre e exibe todas as matrículas
        for (int i = 0; i < enrollments.size(); i++) {
            ui.showMessage("---------- " + (i + 1) + " ----------");
            ui.showMessage(enrollments.get(i).toString());
        }
        ui.showMessage("===================================");
    }

    // ================= PRIVADOS =================


    // Converte string para PaymentType
    private PaymentType parsePaymentType(String input) {
        if (input == null || input.isBlank()) return null;
        if (!input.matches("\\d+")) return null;

        int value = Integer.parseInt(input);
        return PaymentType.fromOption(value);
    }

}

