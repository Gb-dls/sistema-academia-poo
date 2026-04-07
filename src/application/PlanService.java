package application;
import domain.Plan;
import domain.PlanType;
import java.util.ArrayList;
import java.util.Comparator;

public class PlanService {
    ArrayList<Plan> plansArrayList = new ArrayList<Plan>();

    public boolean nameExists(String name){
        for (Plan planoActual : plansArrayList) {
            if (planoActual.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /*Verificação de PlanType pendente*/
    public OperationResult registerPlan(String name, String description, PlanType plan, int minDurationMonths, double pricePerMonth){

        if(nameExists(name)){
           return new OperationResult(false, "Já há um plano com esse nome");
        }
        if(minDurationMonths <= 0) {
            return new OperationResult(false, "A duração mínima deve ser maior que zero.");
        }
        if(pricePerMonth <= 0){
           return new OperationResult(false, "O preço deve ser positivo.");
        }

        Plan newPlan= new Plan(name, description, plan, minDurationMonths, pricePerMonth);
        plansArrayList.add(newPlan);
        // Ordenação da lista por ordem alfabetica
        plansArrayList.sort(Comparator.comparing(Plan::getName));
        return new OperationResult(true, "Plano cadastrado com sucesso!", newPlan);
    }

    public Plan findByName(String name) {
        for (Plan planoActual : plansArrayList) {
            if (planoActual.getName().equalsIgnoreCase(name)) {
                return planoActual;
            }
        }
        return null;
    }

    public OperationResult updatePrice(String name, double newPrice){
        Plan planUpdate = findByName(name);

        if(planUpdate == null){
            return new OperationResult(false, "O plano não foi encontrado.");
        }

        if(newPrice <= 0){
            return new OperationResult(false, "O preço deve ser positivo.");
        }

        planUpdate.setUpdatePrice(newPrice);
        return new OperationResult(true, "O preço do plano " + name + " foi atualizado para R$ " + newPrice);
    }

    public ArrayList<Plan> listPlans() {
        return plansArrayList;
    }

}
