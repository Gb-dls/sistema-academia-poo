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
        if (cpf.isEmpty()) {
            ui.showError("CPF não informado. Matrícula cancelada.");
            return;
        }


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
        if (planName.isEmpty()) {
            ui.showError("Nome do plano não informado. Matrícula cancelada.");
            return;
        }


        // Usa o tipo explícito OperationResult no lugar do 'var'
        OperationResult planResult = fitManager.findPlanByName(planName);

        if (!planResult.isSuccess()) {
            ui.showError(planResult.getMessage());
            return;
        }

        // Converte retorno para Plan
        Plan plan = (Plan) planResult.getData();

        String dateInput     = ui.getInput("Data de início (dd/MM/yyyy):");
        if (dateInput.isEmpty()) {
            ui.showError("Data de início não informada. Matrícula cancelada.");
            return;
        }

        String durationInput = ui.getInput("Duração (em meses):");
        if (durationInput.isEmpty()) {
            ui.showError("Duração não informada. Matrícula cancelada.");
            return;
        }

        String paymentInput  = ui.getInput("Valor do pagamento inicial (ex: 99.90):");
        if (paymentInput.isEmpty()) {
            ui.showError("Valor do pagamento não informado. Matrícula cancelada.");
            return;
        }


        ui.showMessage("""
            Tipos de pagamento:
            1 - Dinheiro
            2 - Cartão de débito
            3 - Cartão de crédito
            4 - PIX
            """);

        String typeInput = ui.getInput("Escolha o tipo de pagamento:");
        if (typeInput.isEmpty()) {
            ui.showError("Tipo de pagamento não informado. Matrícula cancelada.");
            return;
        }

        if (!typeInput.matches("[1-4]")) {
            ui.showError("Tipo de pagamento inválido!");
            return;
        }
        int option = Integer.parseInt(typeInput);

        // Coleta de dados dinâmicos baseados na escolha do usuário
        String extra1 = "", extra2 = "", extra3 = "";
        switch (option) {
            case 1:
                extra1 = ui.getInput("Valor em dinheiro entregue pelo cliente (para cálculo do troco):");
                if (extra1.isEmpty()) {
                    ui.showError("Valor em dinheiro não informado. Matrícula cancelada.");
                    return;
                }
                break;
            case 2:
                extra1 = ui.getInput("Nome do titular do cartão de débito:");
                if (extra1.isEmpty()) {
                    ui.showError("Nome do titular não informado. Matrícula cancelada.");
                    return;
                }
                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
                if (extra2.isEmpty()) {
                    ui.showError("Dígitos do cartão não informados. Matrícula cancelada.");
                    return;
                }
                break;
            case 3:
                extra1 = ui.getInput("Nome do titular do cartão de crédito:");
                if (extra1.isEmpty()) {
                    ui.showError("Nome do titular não informado. Matrícula cancelada.");
                    return;
                }
                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
                if (extra2.isEmpty()) {
                    ui.showError("Dígitos do cartão não informados. Matrícula cancelada.");
                    return;
                }
                extra3 = ui.getInput("Quantidade de parcelas desejada:");
                if (extra3.isEmpty()) {
                    ui.showError("Quantidade de parcelas não informada. Matrícula cancelada.");
                    return;
                }
                break;
            case 4:
                extra1 = ui.getInput("Informe a chave PIX utilizada:");
                if (extra1.isEmpty()) {
                    ui.showError("Chave PIX não informada. Matrícula cancelada.");
                    return;
                }
                break;
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
        if (codeInput.isEmpty()) {
            ui.showError("Código não informado. Pagamento cancelado.");
            return;
        }

        String amountInput = ui.getInput("Valor do pagamento (ex: 99.90):");
        if (amountInput.isEmpty()) {
            ui.showError("Valor não informado. Pagamento cancelado.");
            return;
        }


        ui.showMessage("""
            Tipos de pagamento:
            1 - Dinheiro
            2 - Cartão de débito
            3 - Cartão de crédito
            4 - PIX
            """);

        String typeInput = ui.getInput("Escolha o tipo de pagamento:");
        if (typeInput.isEmpty()) {
            ui.showError("Tipo de pagamento não informado. Pagamento cancelado.");
            return;
        }

        if (!typeInput.matches("[1-4]")) {
            ui.showError("Tipo de pagamento inválido!");
            return;
        }
        int option = Integer.parseInt(typeInput);

        String extra1 = "", extra2 = "", extra3 = "";
        switch (option) {
            case 1:
                extra1 = ui.getInput("Valor em dinheiro entregue pelo cliente:");
                if (extra1.isEmpty()) {
                    ui.showError("Valor em dinheiro não informado. Pagamento cancelado.");
                    return;
                }
                break;
            case 2:
                extra1 = ui.getInput("Nome do titular do cartão de débito:");
                if (extra1.isEmpty()) {
                    ui.showError("Nome do titular não informado. Pagamento cancelado.");
                    return;
                }
                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
                if (extra2.isEmpty()) {
                    ui.showError("Dígitos do cartão não informados. Pagamento cancelado.");
                    return;
                }
                break;
            case 3:
                extra1 = ui.getInput("Nome do titular do cartão de crédito:");
                if (extra1.isEmpty()) {
                    ui.showError("Nome do titular não informado. Pagamento cancelado.");
                    return;
                }
                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
                if (extra2.isEmpty()) {
                    ui.showError("Dígitos do cartão não informados. Pagamento cancelado.");
                    return;
                }
                extra3 = ui.getInput("Quantidade de parcelas:");
                if (extra3.isEmpty()) {
                    ui.showError("Quantidade de parcelas não informada. Pagamento cancelado.");
                    return;
                }
                break;
            case 4:
                extra1 = ui.getInput("Informe a chave PIX:");
                if (extra1.isEmpty()) {
                    ui.showError("Chave PIX não informada. Pagamento cancelado.");
                    return;
                }
                break;
        }

        var result = fitManager.registerPayment(codeInput, amountInput, option, extra1, extra2, extra3);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

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

    private void cancel() {
        String codeInput = ui.getInput("Código da matrícula:");
        if (codeInput.isEmpty()) {
            ui.showError("Código não informado. Cancelamento abortado.");
            return;
        }

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
    }
}