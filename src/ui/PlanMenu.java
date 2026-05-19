package ui;

import domain.plan.Plan;
import application.FitManager;
import application.OperationResult;

import java.util.ArrayList;

public class PlanMenu {

    private final UserInterface ui;
    private final FitManager fitManager;

    public PlanMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void start() {
        String option;
        do {
            ui.showMenu("GERENCIAR PLANOS", """
                    1 - Cadastrar novo plano
                    2 - Consultar por nome
                    3 - Alterar preço
                    4 - Listar todos
                    5 - Voltar
                    """);
            option = ui.getInput("");
            switch (option) {
                case "1" -> registerPlan();
                case "2" -> findByName();
                case "3" -> updatePrice();
                case "4" -> listAll();
                case "5" -> ui.showMessage("Voltando ao menu principal...");
                default  -> ui.showError("Opção inválida!");
            }
        } while (!option.equals("5"));
    }

    private void registerPlan() {
        String name = ui.getInput("Nome do plano:");
        if (name.isEmpty()) {
            ui.showError("Nome não informado. Cadastro cancelado.");
            return;
        }

        String description = ui.getInput("Descrição:");
        if (description.isEmpty()) {
            ui.showError("Descrição não informada. Cadastro cancelado.");
            return;
        }
        ui.showMessage("""
                Tipos de plano:
                1 - Mensal
                2 - Trimestral
                3 - Semestral
                4 - Anual
                """);
        String typeInput = ui.getInput("Escolha o tipo:");
        if (typeInput.isEmpty()) {
            ui.showError("Tipo não informado. Cadastro cancelado.");
            return;
        }


        // Coleta dados adicionais
        String durationInput = ui.getInput("Duração mínima (em meses):");
        if (durationInput.isEmpty()) {
            ui.showError("Duração não informada. Cadastro cancelado.");
            return;
        }

        String priceInput    = ui.getInput("Preço mensal (ex: 99.90):");
        if (priceInput.isEmpty()) {
            ui.showError("Preço não informado. Cadastro cancelado.");
            return; }


        // Envia dados para a camada de negócio
        var result = fitManager.registerPlan(name, description, typeInput, durationInput, priceInput);

        // Exibe resultado da operação

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void findByName() {
        String name = ui.getInput("Nome do plano:");
        if (name.isEmpty()) {
            ui.showError("Nome não informado. Consulta cancelada.");
            return;
        }

        OperationResult result = fitManager.findPlanByName(name);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void updatePrice() {
        String name = ui.getInput("Nome do plano:");
        if (name.isEmpty()) {
            ui.showError("Nome não informado. Alteração cancelada.");
            return;
        }

        String priceInput = ui.getInput("Novo preço mensal (ex: 99.90):");
        if (priceInput.isEmpty()) {
            ui.showError("Preço não informado. Alteração cancelada.");
            return;
        }

        OperationResult result = fitManager.updatePlanPrice(name, priceInput);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

    private void listAll() {
        ArrayList<Plan> plans = fitManager.listPlans();
        if (plans.isEmpty()) {
            ui.showError("Nenhum plano cadastrado.");
            return;
        }
        ui.showMessage("===== LISTA DE PLANOS =====");
        for (int i = 0; i < plans.size(); i++) {
            ui.showMessage("---------- " + (i + 1) + " ----------");
            ui.showMessage(plans.get(i).toString());
        }
    }

}