package ui;

import domain.plan.Plan;
import application.FitManager;

import java.util.ArrayList;

// Classe responsável pelo menu de PLANOS
// Faz a interface entre o usuário e o FitManager (camada de serviço)
public class PlanMenu {

    // Interface responsável por entrada e saída de dados com o usuário
    private final UserInterface ui;
    private final FitManager fitManager;

    // Construtor das dependências necessárias
    public PlanMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    // ================= MENU PRINCIPAL =================
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

        // Coleta dados básicos do plano
        String name        = ui.getInput("Nome do plano:");
        String description = ui.getInput("Descrição:");

        // Exibe opções de tipo de plano
        ui.showMessage(
                """
                Tipos de plano:
                1 - Mensal
                2 - Trimestral
                3 - Semestral
                4 - Anual
                """
        );

        String typeInput = ui.getInput("Escolha o tipo:");

        // Coleta dados adicionais
        String durationInput = ui.getInput("Duração mínima (em meses):");
        String priceInput    = ui.getInput("Preço mensal (ex: 99.90):");

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

    // ================= CONSULTAR POR NOME =================
    private void findByName() {

        // Solicita nome do plano
        String name = ui.getInput("Nome do plano:");

        // Busca plano no sistema
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

        // Nome do plano a ser alterado
        String name = ui.getInput("Nome do plano:");

        // Novo preço informado pelo usuário
        String priceInput = ui.getInput("Novo preço mensal (ex: 99.90):");

        // Envia direto como String quem valida é o service
        var result = fitManager.updatePlanPrice(name, priceInput);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= LISTAR TODOS =================
    private void listAll() {

        // Busca todos os planos cadastrados
        ArrayList<Plan> plans = fitManager.listPlans();

        if (plans.isEmpty()) {
            ui.showError("Nenhum plano cadastrado.");
            return;
        }

        ui.showMessage("===== LISTA DE PLANOS =====");
        // Percorre e exibe todos os planos
        for (int i = 0; i < plans.size(); i++) {
            ui.showMessage("---------- " + (i + 1) + " ----------");
            ui.showMessage(plans.get(i).toString());
        }
        ui.showMessage("===========================");
    }

}