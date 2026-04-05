package application;
import domain.Plan;
import domain.PlanType;

import java.util.ArrayList;

public class PlanService {
    ArrayList<Plan> plansArrayList = new ArrayList<Plan>();

    /*Verificação de PlanType pendente*/

    public OperationResult registerPlan(String name, String description, PlanType plan, int minDurationMonths, double pricePerMonth){

        if(pricePerMonth <= 0){
            return new OperationResult(false, "O preço deve ser positivo.", null);
        }
        if(minDurationMonths <= 0) {
            return new OperationResult(false, "A duração mínima deve ser maior que zero.", null);
        }

        Plan novoPlano = new Plan(name, description, plan, minDurationMonths, pricePerMonth);
        plansArrayList.add(novoPlano);
        return new OperationResult(true, "Plano cadastrado com sucesso!", null);
    }

    public Plan findByName(String name){
    }
}
