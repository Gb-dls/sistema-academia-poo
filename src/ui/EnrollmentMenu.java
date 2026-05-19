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
    @ Objetivo: Centralizar a captura de dados de pagamento dinâmicos de enroll() e registerPayment() com validação anti-nulo
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
        if (typeInput.isEmpty()) {
            ui.showError("Tipo de pagamento não informado. Operação cancelada.");
            return null;
        }

        if (!typeInput.matches("[1-4]")) {
            ui.showError("Tipo de pagamento inválido!");
            return null;
        }

        String option = typeInput;
        String extra1 = "", extra2 = "", extra3 = "";

        switch (Integer.parseInt(option)) {
            case 1 -> {
                extra1 = ui.getInput("Valor em dinheiro entregue pelo cliente (para cálculo do troco):");
                if (extra1.isEmpty()) {
                    ui.showError("Valor em dinheiro não informado. Operação cancelada.");
                    return null;
                }
            }
            case 2 -> {
                extra1 = ui.getInput("Nome do titular do cartão de débito:");
                if (extra1.isEmpty()) {
                    ui.showError("Nome do titular não informado. Operação cancelada.");
                    return null;
                }
                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
                if (extra2.isEmpty()) {
                    ui.showError("Dígitos do cartão não informados. Operação cancelada.");
                    return null;
                }
            }
            case 3 -> {
                extra1 = ui.getInput("Nome do titular do cartão de crédito:");
                if (extra1.isEmpty()) {
                    ui.showError("Nome do titular não informado. Operação cancelada.");
                    return null;
                }
                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
                if (extra2.isEmpty()) {
                    ui.showError("Dígitos do cartão não informados. Operação cancelada.");
                    return null;
                }
                extra3 = ui.getInput("Quantidade de parcelas desejada:");
                if (extra3.isEmpty()) {
                    ui.showError("Quantidade de parcelas não informada. Operação cancelada.");
                    return null;
                }
            }
            case 4 -> {
                extra1 = ui.getInput("Informe a chave PIX utilizada:");
                if (extra1.isEmpty()) {
                    ui.showError("Chave PIX não informada. Operação cancelada.");
                    return null;
                }
            }
        }

        return new String[]{option, extra1, extra2, extra3};
    }

    /*
    @ enroll
    @ Objetivo: Coletar os dados da matrícula validando campos em branco
    */
    private void enroll() {
        String cpf = ui.getInput("CPF do aluno:");
        if (cpf.isEmpty()) {
            ui.showError("CPF não informado. Matrícula cancelada.");
            return;
        }

        OperationResult studentResult = fitManager.findStudentByCpf(cpf);
        if (!studentResult.isSuccess()) {
            ui.showError(studentResult.getMessage());
            return;
        }
        Student student = (Student) studentResult.getData();

        String planName = ui.getInput("Nome do plano:");
        if (planName.isEmpty()) {
            ui.showError("Nome do plano não informado. Matrícula cancelada.");
            return;
        }

        OperationResult planResult = fitManager.findPlanByName(planName);
        if (!planResult.isSuccess()) {
            ui.showError(planResult.getMessage());
            return;
        }
        Plan plan = (Plan) planResult.getData();

        String dateInput = ui.getInput("Data de início (dd/MM/yyyy):");
        if (dateInput.isEmpty()) {
            ui.showError("Data de início não informada. Matrícula cancelada.");
            return;
        }

        String durationInput = ui.getInput("Duração (em meses):");
        if (durationInput.isEmpty()) {
            ui.showError("Duração não informada. Matrícula cancelada.");
            return;
        }

        String paymentInput = ui.getInput("Valor do pagamento inicial (ex: 99.90):");
        if (paymentInput.isEmpty()) {
            ui.showError("Valor do pagamento não informado. Matrícula cancelada.");
            return;
        }

        String[] paymentData = preparePaymentDetails();
        if (paymentData == null) return;

        int option = Integer.parseInt(paymentData[0]);

        var result = fitManager.enroll(student, plan, dateInput, durationInput, paymentInput, option, paymentData[1], paymentData[2], paymentData[3]);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

    /*
    @ registerPayment
    @ Objetivo: Capturar o CPF do aluno, localizar contrato com débito pendente automaticamente e registrar um pagamento.
    */
    private void registerPayment() {
        String cpf = ui.getInput("CPF do aluno:");
        if (cpf.isEmpty()) {
            ui.showError("CPF não informado. Pagamento cancelado.");
            return;
        }

        var activeResult = fitManager.findActiveEnrollmentByStudent(cpf);
        String codeInput;

        if (activeResult.isSuccess()) {
            Enrollment enrollment = (Enrollment) activeResult.getData();
            codeInput = String.valueOf(enrollment.getCode());
            ui.showMessage("Matrícula ATIVA localizada! Código: " + codeInput);
        } else {
            ui.showMessage("Nenhuma matrícula ATIVA encontrada. Se for um acerto de contas de cancelamento:");
            codeInput = ui.getInput("Digite o código da matrícula que deseja pagar:");
        }

        if (codeInput.isBlank()) {
            ui.showError("Código da matrícula não informado. Pagamento abortado.");
            return;
        }

        String amountInput = ui.getInput("Valor do pagamento (ex: 99.90):");
        if (amountInput.isEmpty()) {
            ui.showError("Valor não informado. Pagamento cancelado.");
            return;
        }

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
        if (cpf.isEmpty()) {
            ui.showError("CPF não informado. Consulta cancelada.");
            return;
        }
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
    @ Objetivo: Encontrar a matrícula ativa automaticamente por Cpf e solicitar o cancelamento.
    */
    private void cancel() {
        String cpf = ui.getInput("CPF do aluno para cancelamento:");
        if (cpf.isEmpty()) {
            ui.showError("CPF não informado. Cancelamento abortado.");
            return;
        }

        var activeResult = fitManager.findActiveEnrollmentByStudent(cpf);
        if (!activeResult.isSuccess()) {
            ui.showError(activeResult.getMessage());
            return;
        }

        Enrollment enrollment = (Enrollment) activeResult.getData();
        String codeInput = String.valueOf(enrollment.getCode());

        ui.showMessage("Matrícula ativa localizada! Encerrando contrato código: " + codeInput);

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
    }
}