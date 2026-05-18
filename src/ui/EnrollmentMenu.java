package ui;

import domain.Enrollment;
import domain.Plan;
import domain.Student;
import application.FitManager;
import application.OperationResult;
import java.util.ArrayList;

public class EnrollmentMenu {

    private final UserInterface ui;
    private final FitManager fitManager;

    public EnrollmentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void start() {
        String option;
        do {
            ui.showMenu("GERENCIAR MATRÍCULAS", """
                    1 - Realizar matrícula
                    2 - Registrar pagamento
                    3 - Cancelar matrícula
                    4 - Consultar matrícula ativa
                    5 - Listar histórico
                    6 - Voltar
                    """);
            option = ui.getInput("");
            switch (option) {
                case "1" -> enroll();
                case "2" -> registerPayment();
                case "3" -> cancel();
                case "4" -> findActiveByStudent();
                case "5" -> listAll();
                case "6" -> ui.showMessage("Voltando ao menu principal...");
                default -> ui.showError("Opção inválida!");
            }
        } while (!option.equals("6"));
    }

    /*
    @ enroll
    @ Objetivo: Coletar os dados da matrícula e escolher o tipo de pagamento
    */
    private void enroll() {
        // Solicita CPF do aluno
        String cpf = ui.getInput("CPF do aluno:");

        // Usa o tipo explícito OperationResult no lugar do 'var'
        OperationResult studentResult = fitManager.findStudentByCpf(cpf);

        if (!studentResult.isSuccess()) {
            ui.showError(studentResult.getMessage());
            return;
        }

        // Converte retorno para Student
        Student student = (Student) studentResult.getData();

        // Solicita plano
        String planName = ui.getInput("Nome do plano:");

        // Usa o tipo explícito OperationResult no lugar do 'var'
        OperationResult planResult = fitManager.findPlanByName(planName);

        if (!planResult.isSuccess()) {
            ui.showError(planResult.getMessage());
            return;
        }

        // Converte retorno para Plan
        Plan plan = (Plan) planResult.getData();

        String dateInput     = ui.getInput("Data de início (dd/MM/yyyy):");
        String durationInput = ui.getInput("Duração (em meses):");
        String paymentInput  = ui.getInput("Valor do pagamento inicial (ex: 99.90):");

        ui.showMessage("""
            Tipos de pagamento:
            1 - Dinheiro
            2 - Cartão de débito
            3 - Cartão de crédito
            4 - PIX
            """);

        String typeInput = ui.getInput("Escolha o tipo de pagamento:");
        if (!typeInput.matches("[1-4]")) {
            ui.showError("Tipo de pagamento inválido!");
            return;
        }
        int option = Integer.parseInt(typeInput);

        // Coleta de dados dinâmicos baseados na escolha do usuário
        String extra1 = "", extra2 = "", extra3 = "";
        switch (option) {
            case 1 -> extra1 = ui.getInput("Valor em dinheiro entregue pelo cliente (para cálculo do troco):");
            case 2 -> {
                extra1 = ui.getInput("Nome do titular do cartão de débito:");
                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
            }
            case 3 -> {
                extra1 = ui.getInput("Nome do titular do cartão de crédito:");
                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
                extra3 = ui.getInput("Quantidade de parcelas desejada:");
            }
            case 4 -> extra1 = ui.getInput("Informe a chave PIX utilizada:");
        }

        var result = fitManager.enroll(student, plan, dateInput, durationInput, paymentInput, option, extra1, extra2, extra3);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

    /*
    @ registerPayment
    @ Objetivo: Coletar os dados de pagamentos adicionais avulsos capturando as especificidades de cada subclasse
    */
    private void registerPayment() {
        String codeInput   = ui.getInput("Código da matrícula:");
        String amountInput = ui.getInput("Valor do pagamento (ex: 99.90):");

        ui.showMessage("""
            Tipos de pagamento:
            1 - Dinheiro
            2 - Cartão de débito
            3 - Cartão de crédito
            4 - PIX
            """);

        String typeInput = ui.getInput("Escolha o tipo de pagamento:");
        if (!typeInput.matches("[1-4]")) {
            ui.showError("Tipo de pagamento inválido!");
            return;
        }
        int option = Integer.parseInt(typeInput);

        String extra1 = "", extra2 = "", extra3 = "";
        switch (option) {
            case 1 -> extra1 = ui.getInput("Valor em dinheiro entregue pelo cliente:");
            case 2 -> {
                extra1 = ui.getInput("Nome do titular do cartão de débito:");
                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
            }
            case 3 -> {
                extra1 = ui.getInput("Nome do titular do cartão de crédito:");
                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
                extra3 = ui.getInput("Quantidade de parcelas:");
            }
            case 4 -> extra1 = ui.getInput("Informe a chave PIX:");
        }

        var result = fitManager.registerPayment(codeInput, amountInput, option, extra1, extra2, extra3);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

    private void findActiveByStudent() {
        String cpf = ui.getInput("CPF do aluno:");
        var result = fitManager.findActiveEnrollmentByStudent(cpf);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void cancel() {
        String codeInput = ui.getInput("Código da matrícula:");
        var result = fitManager.cancelEnrollment(codeInput);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

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
}