package application;

import domain.plan.*;
import exceptions.*;
import java.util.ArrayList;
import persistence.PlanRepository;

// Classe responsável pela lógica de negócio dos planos
// Aqui ficam regras de cadastro, busca, atualização e validação de planos
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
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
        if (planRepository.nameExists(name)) {
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

        planRepository.add(newPlan);
        planRepository.sortByName();

        return new OperationResult<>(true, "Plano " + name + " cadastrado com sucesso!", newPlan);
    }

    // ================= BUSCA =================
    public OperationResult<Plan> findByName(String name) {
        Plan plan = planRepository.findByName(name);
        if (plan == null){
            return new OperationResult<>(false, "Plano não encontrado.");
        }
        return new OperationResult<>(true, "Plano encontrado.", plan);
    }

    // ================= ATUALIZAÇÃO =================
    public OperationResult<Plan> updatePrice(String name, String priceStr) throws ValidationException, BusinessException {

        Plan plan = planRepository.findByName(name);

        if (plan == null) {
            throw new BusinessException("O plano informado não foi encontrado no sistema.");
        }

        double newPrice = parseDouble(priceStr);
        if (newPrice <= 0) {
            throw new InvalidFormatFieldException("Novo preço", "Valor numérico positivo");
        }

        plan.updatePrice(newPrice);
        return new OperationResult<>(true, "O preço do plano " + name + " foi atualizado para R$ " + newPrice, plan);
    }

    // ================= LISTAGEM =================
    public OperationResult<ArrayList<Plan>> listPlans() {
        ArrayList<Plan> list = planRepository.listAll();
        if (list.isEmpty()){
            return new OperationResult<>(false, "Nenhum plano cadastrado.");
        }
        return new OperationResult<>(true, "Lista carregada.", list);
    }

    // ================= PRIVADOS =================
    private int parseInt(String input) {
        if (input == null || input.isBlank()){
            return -1;
        }
        if (!input.matches("\\d+")){
            return -1;
        }
        return Integer.parseInt(input);
    }

    private double parseDouble(String input) {
        if (input == null || input.isBlank()){
            return -1;
        }
        if (!input.matches("\\d+(\\.\\d+)?")){
            return -1;
        }
        return Double.parseDouble(input);
    }
}