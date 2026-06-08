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
        int option;
        do {
            ui.showMenu("GERENCIAR PLANOS", """
                    1 - Cadastrar novo plano
                    2 - Consultar por nome
                    3 - Alterar preço
                    4 - Listar todos
                    5 - Voltar
                    """);
            option = ui.getInt("");
            switch (option) {
                case 1 -> registerPlan();
                case 2 -> findByName();
                case 3 -> updatePrice();
                case 4 -> listAll();
                case 5 -> ui.showMessage("Voltando ao menu principal...");
                default  -> ui.showError("Opção inválida!");
            }
        } while (option != 5);
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
        int type = ui.getInt("Escolha o tipo:");
        if (type == -1) {
            ui.showError("Operação cancelada pelo usuário. Cadastro cancelado.");
            return;
        }

        // Coleta dados adicionais
        int duration = ui.getInt("Duração mínima (em meses):");
        if (duration == -1) {
            ui.showError("Operação cancelada pelo usuário. Cadastro cancelado.");
            return;
        }

        double price = ui.getDouble("Preço mensal (ex: 99.90):");
        if (price == -1.0) {
            ui.showError("Operação cancelada pelo usuário. Cadastro cancelado.");
            return;
        }

        String typeInput = String.valueOf(type);
        String durationInput = String.valueOf(duration);
        String priceInput = String.valueOf(price);

        // Envia dados para a camada de negócio
        OperationResult<Plan> result = fitManager.registerPlan(name, description, typeInput, durationInput, priceInput);

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

        OperationResult<Plan> result = fitManager.findPlanByName(name);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            Plan plan = result.getData();
            ui.showMessage(plan.toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void updatePrice(){

        String name = ui.getInput("Nome do plano:");

        if (name.isEmpty()){
            ui.showError("Nome não informado. Alteração cancelada.");
            return;
        }

        double price = ui.getDouble("Novo preço mensal (ex: 99.90):");
        if (price == -1.0) {
            ui.showError("Operação cancelada pelo usuário. Alteração cancelada.");
            return;
        }

        String priceInput = String.valueOf(price);
        OperationResult<Plan> result = fitManager.updatePlanPrice(name, priceInput);

        if (result.isSuccess()){
            ui.showMessage(result.getMessage());
            Plan plan = result.getData();
            if(plan != null){
                ui.showMessage(plan.toString());
            }
        }else{
            ui.showError(result.getMessage());
        }
    }

    private void listAll() {
        OperationResult<ArrayList<Plan>> result = fitManager.listPlans();
        if (!result.isSuccess()) {
            ui.showError(result.getMessage());
            return;
        }
        ArrayList<Plan> plans = result.getData();
        ui.showMessage("===== LISTA DE PLANOS =====");
        for(int i = 0; i < plans.size(); i++){
            ui.showMessage("---------- " + (i + 1) + " ----------");
            ui.showMessage(plans.get(i).toString());
        }
    }

}