package application;
import domain.Plan;
import domain.PlanType;
import java.util.ArrayList;
import java.util.Comparator;
import application.OperationResult;

public class PlanService {
    // Lista interna de planos cadastrados.
    // Acessível apenas pelos métodos deste serviço — nunca diretamente por outras classes.
    private ArrayList<Plan> plans = new ArrayList<Plan>();

    // Verifica se já existe um plano com o nome informado.
    // A comparação ignora maiúsculas/minúsculas para evitar duplicatas como
    // "Mensal" e "mensal".
    public boolean nameExists(String name){
        for (Plan current : plans) {
            if (current.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    // Registra um novo plano após validar todos os campos obrigatórios.
    // Retorna OperationResult com sucesso e o objeto Plan criado,
    // ou com falha e mensagem descritiva caso alguma validação não passe.
    public OperationResult registerPlan(String name, String description, PlanType type, int minDurationMonths, double pricePerMonth){

        if (type == null) {
            return new OperationResult(false, "Tipo de plano inválido.");
        }
        if (name == null || name.isBlank()) {
            return new OperationResult(false, "O nome do plano não pode ser vazio.");
        }
        if (description == null || description.isBlank()) {
            return new OperationResult(false, "A descrição não pode ser vazia.");
        }
        if(nameExists(name)){
           return new OperationResult(false, "Já há um plano com esse nome");
        }
        if(minDurationMonths <= 0) {
            return new OperationResult(false, "A duração mínima deve ser maior que zero.");
        }
        if(pricePerMonth <= 0){
           return new OperationResult(false, "O preço deve ser positivo.");
        }

        Plan newPlan= new Plan(name, description, type, minDurationMonths, pricePerMonth);
        plans.add(newPlan);
        // Mantém a lista ordenada alfabeticamente após cada inserção
        // para facilitar a exibição nas listagens.
        plans.sort(Comparator.comparing(Plan::getName));
        return new OperationResult(true, "Plano cadastrado com sucesso!", newPlan);
    }

    // Busca um plano pelo nome e retorna null se não encontrado
    public Plan findByName(String name) {
        for (Plan current : plans) {
            if (current.getName().equalsIgnoreCase(name)) {
                return current;
            }
        }
        return null;
    }

    // Atualiza o preço mensal de um plano existente.
    // Matrículas já criadas não são afetadas pois armazenam
    // o totalPrice calculado no momento da criação.
    public OperationResult updatePrice(String name, double newPrice){
        Plan planUpdate = findByName(name);

        if(planUpdate == null){
            return new OperationResult(false, "O plano não foi encontrado.");
        }

        if(newPrice <= 0){
            return new OperationResult(false, "O preço deve ser positivo.");
        }

        planUpdate.updatePrice(newPrice);
        return new OperationResult(true, "O preço do plano " + name + " foi atualizado para R$ " + newPrice);
    }

    // Retorna uma cópia da lista de planos cadastrados.
    // Uma cópia é retornada para impedir que classes externas
    // modifiquem a coleção interna diretamente.
    public ArrayList<Plan> listPlans() {
        return new ArrayList<>(plans);
    }

}
