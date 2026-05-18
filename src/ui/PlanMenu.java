package ui;

import domain.PlanType;
import domain.Plan;
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
        String name        = ui.getInput("Nome do plano:");
        String description = ui.getInput("Descrição:");
        ui.showMessage("""
                Tipos de plano:
                1 - Mensal
                2 - Trimestral
                3 - Semestral
                4 - Anual
                """);
        String typeInput = ui.getInput("Escolha o tipo:");
        PlanType type = parsePlanType(typeInput);
        if (type == null) {
            ui.showError("Tipo de plano inválido!");
            return;
        }
        String durationInput = ui.getInput("Duração mínima (em meses):");
        String priceInput    = ui.getInput("Preço mensal (ex: 99.90):");

        OperationResult result = fitManager.registerPlan(name, description, type, durationInput, priceInput);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void findByName() {
        String name = ui.getInput("Nome do plano:");
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
        String priceInput = ui.getInput("Novo preço mensal (ex: 99.90):");
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
        ui.showMessage("===========================");
    }

    private PlanType parsePlanType(String input) {
        if (input == null || input.isBlank() || !input.matches("\\d+")) return null;
        int value = Integer.parseInt(input);
        return PlanType.fromOptionValue(value);
    }
}