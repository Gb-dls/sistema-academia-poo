package ui;

import domain.Enrollment;
import domain.plan.Plan;
import domain.Student;
import application.FitManager;
import application.OperationResult;
import exceptions.ValidationException;
import exceptions.BusinessException;
import exceptions.RequiredFieldException;
import exceptions.InvalidPaymentMethodException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

public class EnrollmentMenu {

    private final UserInterface ui;
    private final FitManager fitManager;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EnrollmentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    /*
    @ start
    @ Objetivo: Iniciar e gerenciar o loop principal do menu de matrículas, direcionando as escolhas do usuário
    */
    public void start() {
        int option;
        do {
            ui.showMenu("GERENCIAR MATRÍCULAS", """
                    1 - Realizar matrícula
                    2 - Registrar pagamento
                    3 - Cancelar matrícula
                    4 - Consultar matrícula ativa
                    5 - Listar histórico
                    6 - Voltar
                    """);
            option = ui.getInt("");
            switch (option) {
                case 1 -> enroll();
                case 2 -> registerPayment();
                case 3 -> cancel();
                case 4 -> findActiveByStudent();
                case 5 -> listAll();
                case 6 -> ui.showMessage("Voltando ao menu principal...");
                default -> ui.showError("Opção inválida!");
            }
        } while (option != 6);
    }

    /*
    @ preparePaymentDetails
    @ Objetivo: Centralizar a captura de dados de pagamento dinâmicos de enroll() e registerPayment() com validação
    @ Retorna: Um array onde [0]=opcao, [1]=extra1, [2]=extra2, [3]=extra3. Retorna null se for cancelado pelo usuário.
    */
    private String[] preparePaymentDetails() throws ValidationException {
        ui.showMessage("""
        Tipos de pagamento:
        1 - Dinheiro
        2 - Cartão de débito
        3 - Cartão de crédito
        4 - PIX
        """);

        int typeInput = ui.getInt("Escolha o tipo de pagamento:");
        if (typeInput == -1) return null; // Cancelado pelo usuário

        String option = String.valueOf(typeInput);
        String extra1 = "", extra2 = "", extra3 = "";

        switch (Integer.parseInt(option)) {
            case 1 -> {
                double cash = ui.getDouble("Valor em dinheiro entregue pelo cliente (para cálculo do troco):");
                if (cash == -1.0) return null; // Cancelado
                extra1 = String.valueOf(cash);
            }
            case 2 -> {
                extra1 = ui.getInput("Nome do titular do cartão de débito:");
                if (extra1.isEmpty()) throw new RequiredFieldException("Nome do titular");

                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
                if (extra2.isEmpty()) throw new RequiredFieldException("Dígitos do cartão");
            }
            case 3 -> {
                extra1 = ui.getInput("Nome do titular do cartão de crédito:");
                if (extra1.isEmpty()) throw new RequiredFieldException("Nome do titular");

                extra2 = ui.getInput("Últimos 4 dígitos do cartão:");
                if (extra2.isEmpty()) throw new RequiredFieldException("Dígitos do cartão");

                int installments = ui.getInt("Quantidade de parcelas desejada:");
                if (installments == -1) return null; // Cancelado
                extra3 = String.valueOf(installments);
            }
            case 4 -> {
                extra1 = ui.getInput("Informe a chave PIX utilizada:");
                if (extra1.isEmpty()) throw new RequiredFieldException("Chave PIX");
            }
            default -> {
                throw new InvalidPaymentMethodException(option);
            }
        }

        return new String[]{option, extra1, extra2, extra3};
    }

    /*
    @ enroll
    @ Objetivo: Coletar os dados da matrícula validando campos em branco
    */
    private void enroll() {
        boolean sucesso = false;

        while (!sucesso) {
            try {
                String cpf = ui.getInput("CPF do aluno (ou deixe em branco para cancelar):");
                if (cpf.isEmpty()) {
                    ui.showMessage("Matrícula cancelada.");
                    return;
                }

                // Transforma o OperationResult falso das consultas em Exceção para reiniciar o laço
                OperationResult<Student> studentResult = fitManager.findStudentByCpf(cpf);
                if (!studentResult.isSuccess()) {
                    throw new BusinessException(studentResult.getMessage());
                }
                Student student = studentResult.getData();

                String planName = ui.getInput("Nome do plano:");
                if (planName.isEmpty()) {
                    ui.showMessage("Matrícula cancelada.");
                    return;
                }

                OperationResult<Plan> planResult = fitManager.findPlanByName(planName);
                if (!planResult.isSuccess()) {
                    throw new BusinessException(planResult.getMessage());
                }
                Plan plan = planResult.getData();

                LocalDate startDate = ui.getDate("Data de início");
                if (startDate == null) return;
                String dateInput = startDate.format(dateFormatter);

                int duration = ui.getInt("Duração (em meses):");
                if (duration == -1) return;
                String durationInput = String.valueOf(duration);

                double paymentValue = ui.getDouble("Valor do pagamento inicial (ex: 99.90):");
                if (paymentValue == -1.0) return;
                String paymentInput = String.valueOf(paymentValue);

                String[] paymentData = preparePaymentDetails();
                if (paymentData == null) return; // Se o usuário cancelar no meio do pagamento

                int option = Integer.parseInt(paymentData[0]);

                OperationResult<Enrollment> result = fitManager.enroll(student, plan, dateInput, durationInput, paymentInput, option, paymentData[1], paymentData[2], paymentData[3]);

                ui.showMessage(result.getMessage());
                sucesso = true;

            } catch (ValidationException | BusinessException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("Erro inesperado: " + e.getMessage());
                sucesso = true;
            }
        }
    }

    /*
    @ registerPayment
    @ Objetivo: Capturar o CPF do aluno, localizar contrato com débito pendente automaticamente e registrar um pagamento.
    */
    private void registerPayment() {
        boolean sucesso = false;

        while (!sucesso) {
            try {
                String cpf = ui.getInput("CPF do aluno (ou vazio para cancelar):");
                if (cpf.isEmpty()) return;

                OperationResult<Enrollment> activeResult = fitManager.findActiveEnrollmentByStudent(cpf);
                String codeInput;

                if (activeResult.isSuccess()) {
                    Enrollment enrollment = activeResult.getData();
                    codeInput = String.valueOf(enrollment.getCode());
                    ui.showMessage("Matrícula ATIVA localizada! Código: " + codeInput);
                } else {
                    ui.showMessage("Nenhuma matrícula ATIVA encontrada. Se for um acerto de contas de cancelamento:");
                    int code = ui.getInt("Digite o código da matrícula que deseja pagar:");
                    if (code == -1) return;
                    codeInput = String.valueOf(code);
                }

                double amount = ui.getDouble("Valor do pagamento (ex: 99.90):");
                if (amount == -1.0) return;
                String amountInput = String.valueOf(amount);

                String[] paymentData = preparePaymentDetails();
                if (paymentData == null) return;

                int option = Integer.parseInt(paymentData[0]);

                OperationResult<Enrollment> result = fitManager.registerPayment(codeInput, amountInput, option, paymentData[1], paymentData[2], paymentData[3]);

                ui.showMessage(result.getMessage());
                sucesso = true;

            } catch (ValidationException | BusinessException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("Erro inesperado: " + e.getMessage());
                sucesso = true;
            }
        }
    }

    /*
    @ cancel
    @ Objetivo: Encontrar a matrícula ativa automaticamente por Cpf e solicitar o cancelamento.
    */
    private void cancel() {
        boolean sucesso = false;

        while (!sucesso) {
            try {
                String cpf = ui.getInput("CPF do aluno para cancelamento (ou vazio para voltar):");
                if (cpf.isEmpty()) return;

                OperationResult<Enrollment> activeResult = fitManager.findActiveEnrollmentByStudent(cpf);
                if (!activeResult.isSuccess()) {
                    throw new BusinessException(activeResult.getMessage());
                }

                Enrollment enrollment = activeResult.getData();
                String codeInput = String.valueOf(enrollment.getCode());

                ui.showMessage("Matrícula ativa localizada! Encerrando contrato código: " + codeInput);

                OperationResult<Void> result = fitManager.cancelEnrollment(codeInput);

                ui.showMessage(result.getMessage());
                sucesso = true;

            } catch (ValidationException | BusinessException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("Erro inesperado: " + e.getMessage());
                sucesso = true;
            }
        }
    }

    /*
    @ findActiveByStudent
    @ Objetivo: Solicitar a busca e exibir os dados completos da matrícula ativa de acordo com o Cpf informado
    */
    private void findActiveByStudent() {
        String cpf = ui.getInput("CPF do aluno:");
        if (cpf.isEmpty()) {
            ui.showMessage("Consulta cancelada.");
            return;
        }
        OperationResult<Enrollment> result = fitManager.findActiveEnrollmentByStudent(cpf);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    /*
    @ listAll
    @ Objetivo: Recuperar e listar o histórico de matrículas cadastradas no sistema da academia
    */
    private void listAll() {
        OperationResult<ArrayList<Enrollment>> result = fitManager.listEnrollments();
        if (!result.isSuccess()) {
            ui.showError(result.getMessage());
            return;
        }
        ArrayList<Enrollment> enrollments = result.getData();
        ui.showMessage("===== HISTÓRICO DE MATRÍCULAS =====");
        for(int i = 0; i < enrollments.size(); i++){
            ui.showMessage("---------- " + (i + 1) + " ----------");
            ui.showMessage(enrollments.get(i).toString());
        }
    }
}