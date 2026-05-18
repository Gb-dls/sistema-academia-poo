package ui;

import domain.Enrollment;
import domain.plan.Plan;
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

    /*
    @ start
    @ Objetivo: Iniciar e gerenciar o loop principal do menu de matrículas, direcionando as escolhas do usuário
    */
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
    @ preparePaymentDetails
    @ Objetivo: Centralizar a captura de dados de pagamento dinâmicos de enroll() e registerPayment()
    @ Retorna: Um array onde [0]=opcao, [1]=extra1, [2]=extra2, [3]=extra3. Retorna null se for inválido.
    */
    private String[] preparePaymentDetails() {
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
            return null;
        }

        String option = typeInput;
        String extra1 = "", extra2 = "", extra3 = "";

        switch (Integer.parseInt(option)) {
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

        return new String[]{option, extra1, extra2, extra3};
    }

    /*
    @ enroll
    @ Objetivo: Coletar os dados da matrícula
    */
    private void enroll() {
        String cpf = ui.getInput("CPF do aluno:");
        OperationResult studentResult = fitManager.findStudentByCpf(cpf);
        if (!studentResult.isSuccess()) {
            ui.showError(studentResult.getMessage());
            return;
        }
        Student student = (Student) studentResult.getData();

        String planName = ui.getInput("Nome do plano:");
        OperationResult planResult = fitManager.findPlanByName(planName);
        if (!planResult.isSuccess()) {
            ui.showError(planResult.getMessage());
            return;
        }
        Plan plan = (Plan) planResult.getData();

        String dateInput     = ui.getInput("Data de início (dd/MM/yyyy):");
        String durationInput = ui.getInput("Duração:");
        String paymentInput  = ui.getInput("Valor do pagamento inicial:");

        String[] paymentData = preparePaymentDetails();
        if (paymentData == null) return;

        int option = Integer.parseInt(paymentData[0]);

        var result = fitManager.enroll(student, plan, dateInput, durationInput, paymentInput, option, paymentData[1], paymentData[2], paymentData[3]);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

    /*
    @ registerPayment
    @ Objetivo: Coletar os dados de pagamentos adicionais avulsos capturando as especificidades de cada subclasse
    */
    private void registerPayment() {
        String codeInput = ui.getInput("Código da matrícula:");
        String amountInput = ui.getInput("Valor do pagamento:");

        String[] paymentData = preparePaymentDetails();
        if (paymentData == null) return;

        int option = Integer.parseInt(paymentData[0]);

        var result = fitManager.registerPayment(codeInput, amountInput, option, paymentData[1], paymentData[2], paymentData[3]);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

    /*
    @ findActiveByStudent
    @ Objetivo: Solicitar a busca e exibir os dados completos da matrícula ativa de acordo com o Cpf informado
    */
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

    /*
    @ cancel
    @ Objetivo: Capturar o código de uma matrícula para solicitar o seu cancelamento e exibir o resumo financeiro
    */
    private void cancel() {
        String codeInput = ui.getInput("Código da matrícula:");
        var result = fitManager.cancelEnrollment(codeInput);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

    /*
    @ listAll
    @ Objetivo: Recuperar e listar o histórico de matrículas cadastradas no sistema da academia
    */
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