package application;

import domain.plan.*;
import exceptions.*;
import java.util.ArrayList;
import java.util.Comparator;

// Classe responsável pela lógica de negócio dos planos
// Aqui ficam regras de cadastro, busca, atualização e validação de planos
public class PlanService extends Repository<Plan> {

    // ================= VALIDAÇÕES =================

    public boolean nameExists(String name){
        for (Plan current : items) {
            if (current.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    // ================= CADASTRAR PLANO =================
    public OperationResult<Plan> registerPlan(String name, String description, String typeStr, String minDurationStr, String priceStr) throws ValidationException, BusinessException {

        if (typeStr == null || typeStr.isBlank()) {
            throw new RequiredFieldException("Tipo de plano");
        }
        if (name == null || name.isBlank()) {
            throw new RequiredFieldException("Nome do plano");
        }
        if (description == null || description.isBlank()) {
            throw new RequiredFieldException("Descrição");
        }
        if (nameExists(name)) {
            throw new DuplicatedPlanException(name);
        }

        int minDurationMonths = parseInt(minDurationStr);
        if (minDurationMonths <= 0) {
            throw new InvalidFormatFieldException("Duração", "Número inteiro maior que zero");
        }

        double pricePerMonth = parseDouble(priceStr);
        if (pricePerMonth <= 0) {
            throw new InvalidFormatFieldException("Preço", "Valor numérico positivo");
        }

        int type = parseInt(typeStr);
        Plan newPlan;

        if (type == 1) {
            newPlan = new MonthlyPlan(name, description, minDurationMonths, pricePerMonth);
        } else if (type == 2) {
            newPlan = new QuarterlyPlan(name, description, minDurationMonths, pricePerMonth);
        } else if (type == 3) {
            newPlan = new SemiAnnualPlan(name, description, minDurationMonths, pricePerMonth);
        } else if (type == 4) {
            newPlan = new AnnualPlan(name, description, minDurationMonths, pricePerMonth);
        } else {
            throw new BusinessException("Opção de tipo de plano inválida.");
        }

        items.add(newPlan);
        items.sort(Comparator.comparing(Plan::getName));

        return new OperationResult<>(true, "Plano " + name + " cadastrado com sucesso!", newPlan);
    }

    // ================= BUSCA =================
    public OperationResult<Plan> findByName(String name) {
        for (Plan current : items) {
            if (current.getName().equalsIgnoreCase(name)) {
                return new OperationResult<>(true, "Plano encontrado.", current);
            }
        }
        return new OperationResult<>(false, "Plano não encontrado.");
    }

    // ================= ATUALIZAÇÃO =================
    public OperationResult<Plan> updatePrice(String name, String priceStr) throws ValidationException, BusinessException {

        OperationResult<Plan> result = findByName(name);

        if (!result.isSuccess()) {
            throw new BusinessException("O plano informado não foi encontrado no sistema.");
        }

        Plan planUpdate = result.getData();

        double newPrice = parseDouble(priceStr);
        if (newPrice <= 0) {
            throw new InvalidFormatFieldException("Novo preço", "Valor numérico positivo");
        }

        planUpdate.updatePrice(newPrice);
        return new OperationResult<>(true, "O preço do plano " + name + " foi atualizado para R$ " + newPrice, planUpdate);
    }

    // ================= LISTAGEM =================
    public OperationResult<ArrayList<Plan>> listPlans() {
        if(count() == 0){
            return new OperationResult<>(false, "Nenhum plano cadastrado.");
        }
        return new OperationResult<>(true, "Lista de planos carregada.", listAll());
    }

    @Override
    public void save(String filePath) {
    }

    @Override
    public void load(String filePath) {
    }

    // ================= PRIVADOS =================
    private int parseInt(String input) {
        if (input == null || input.isBlank()) return -1;
        if (!input.matches("\\d+")) return -1;
        return Integer.parseInt(input);
    }

    private double parseDouble(String input) {
        if (input == null || input.isBlank()) return -1;
        if (!input.matches("\\d+(\\.\\d+)?")) return -1;
        return Double.parseDouble(input);
    }
}