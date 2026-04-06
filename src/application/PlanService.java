package application;
import domain.Plan;
import domain.PlanType;
import java.util.ArrayList;

public class PlanService {
    ArrayList<Plan> plansArrayList = new ArrayList<Plan>();


    /*Verificação de PlanType pendente*/
    public boolean/*OperationResult*/ registerPlan(String name, String description, PlanType plan, int minDurationMonths, double pricePerMonth){

        if(minDurationMonths <= 0) {
            //return new OperationResult(false, "A duração mínima deve ser maior que zero.", null);
            System.out.println("[ERRO]: A duração mínima deve ser maior que zero.");
            return false;
        }
        if(pricePerMonth <= 0){
            //return new OperationResult(false, "O preço deve ser positivo.", null);
            System.out.println("[ERRO]: O preço deve ser positivo.");
            return false;
        }

        Plan novoPlano = new Plan(name, description, plan, minDurationMonths, pricePerMonth);
        plansArrayList.add(novoPlano);
        System.out.println("plano cadastrado");
        //return new OperationResult(true, "Plano cadastrado com sucesso!", null);
        return true;
    }

    public Plan findByName(String name) {
        for (Plan planoActual : plansArrayList) {
            if (planoActual.getName().equalsIgnoreCase(name)) {
                return planoActual;
            }
        }
        return null;
    }

    public boolean /*OperationResult*/ updatePrice(String name, double newPrice){
        Plan planUpdate = findByName(name);

        if(planUpdate == null){
            //return new OperationResult(false, "O plano não foi encontrado.", null);
            return false;
        }

        if(newPrice <= 0){
            //return new OperationResult(false, "O preço deve ser positivo.", null);
            System.out.println("[ERRO]: O preço deve ser positivo.");
            return false;
        }

        planUpdate.setUpdatePrice(newPrice);
        System.out.println("[SUCESSO]: O preço do plano " + name + " foi atualizado para R$ " + newPrice);
        return true;
        //return new OperationResult(true, "O preço do plano " + name + " foi atualizado para R$ " + newPrice, null);
    }


}
