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

    // Método principal do menu de planos
    public void start() {

        String option;

        do {
            ui.showMenu(
                    "GERENCIAR PLANOS",
                    """
                    1 - Cadastrar novo plano  
                    2 - Consultar por nome
                    3 - Alterar preço
                    4 - Listar todos
                    5 - Voltar
                    """
            );

            option = ui.getInput("");

            switch (option) {
                case "1" -> registerPlan();     //Registrar Plano

                case "2" -> findByName();       //Recuperar pelo nome do plano

                case "3" -> updatePrice();      //Atualizar Plano

                case "4" -> listAll();          //Listar Todos os planos

                case "5" -> ui.showMessage("Voltando ao menu principal...");
                default  -> ui.showError("Opção inválida!");
            }

        } while (!option.equals("5"));
    }

    // ================= CADASTRAR PLANO =================
    private void registerPlan() {
        String name        = ui.getInput("Nome do plano");
        String description = ui.getInput("Descrição");

        ui.showMessage(
                """
                Tipos de plano:
                1 - Mensal
                2 - Trimestral
                3 - Semestral
                4 - Anual
                """
        );

        String typeInput = ui.getInput("Escolha o tipo");
        PlanType type = parsePlanType(typeInput);

        if (type == null) {
            ui.showError("Tipo de plano inválido!");
            return;
        }

        String durationInput = ui.getInput("Duração mínima (em meses)");
        int minDuration = parseInt(durationInput);

        if (minDuration <= 0) {
            ui.showError("Duração mínima inválida!");
            return;
        }

        String priceInput = ui.getInput("Preço mensal (ex: 99.90)");
        double price = parseDouble(priceInput);

        if (price <= 0) {
            ui.showError("Preço inválido!");
            return;
        }

        var result = fitManager.registerPlan(name, description, type, minDuration, price);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= CONSULTAR POR NOME =================
    private void findByName() {
        String name = ui.getInput("Nome do plano");
        var result = fitManager.findPlanByName(name);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= ALTERAR PREÇO =================
    private void updatePrice() {
        String name = ui.getInput("Nome do plano");

        String priceInput = ui.getInput("Novo preço mensal (ex: 99.90)");
        double newPrice = parseDouble(priceInput);

        if (newPrice <= 0) {
            ui.showError("Preço inválido!");
            return;
        }

        var result = fitManager.updatePlanPrice(name, newPrice);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= LISTAR TODOS =================
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

    // ================= PRIVADOS =================

    // Converte string para PlanType sem usar exceção
    private PlanType parsePlanType(String input) {
        if (input == null || input.isBlank()) return null;

        if (!input.matches("\\d+")) return null;

        int value = Integer.parseInt(input);
        return PlanType.fromOptionValue(value);
    }

    // Converte string para int sem usar exceção
    private int parseInt(String input) {
        if (input == null || input.isBlank()) return -1;
        if (!input.matches("\\d+")) return -1;
        return Integer.parseInt(input);
    }

    // Converte string para double sem usar exceção
    private double parseDouble(String input) {
        if (input == null || input.isBlank()) return -1;
        if (!input.matches("\\d+(\\.\\d+)?")) return -1;
        return Double.parseDouble(input);
    }
}