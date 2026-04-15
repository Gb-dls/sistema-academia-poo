package application;
import domain.Plan;
import domain.PlanType;
import java.util.ArrayList;
import java.util.Comparator;
import application.OperationResult;

// Classe responsável pela lógica de negócio dos planos
// Aqui ficam regras de cadastro, busca, atualização e validação de planos
public class PlanService {

    // ================= ATRIBUTOS =================

    // Lista interna de planos cadastrados.
    // Acessível apenas pelos métodos deste serviço — nunca diretamente por outras classes.
    private ArrayList<Plan> plans = new ArrayList<Plan>();

    // ================= VALIDAÇÕES =================

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


    // ================= CADASTRAR PLANO =================
    // Registra um novo plano no sistema
    public OperationResult registerPlan(String name, String description, PlanType type, String minDurationStr, String priceStr) {

        // Validação do tipo de plano
        if (type == null) {
            return new OperationResult(false, "Tipo de plano inválido.");
        }

        // Validação do nome
        if (name == null || name.isBlank()) {
            return new OperationResult(false, "O nome do plano não pode ser vazio.");
        }

        // Validação da descrição
        if (description == null || description.isBlank()) {
            return new OperationResult(false, "A descrição não pode ser vazia.");
        }

        // Evita duplicação de planos
        if (nameExists(name)) {
            return new OperationResult(false, "Já há um plano com esse nome.");
        }

        // Converte e valida duração mínima
        int minDurationMonths = parseInt(minDurationStr);
        if (minDurationMonths <= 0) {
            return new OperationResult(false, "A duração mínima deve ser maior que zero.");
        }

        // Converte e valida preço
        double pricePerMonth = parseDouble(priceStr);
        if (pricePerMonth <= 0) {
            return new OperationResult(false, "O preço deve ser positivo.");
        }

        // Criação do plano após validações
        Plan newPlan = new Plan(name, description, type, minDurationMonths, pricePerMonth);

        // Adiciona na lista
        plans.add(newPlan);

        // Mantém lista ordenada por nome
        plans.sort(Comparator.comparing(Plan::getName));

        return new OperationResult(true, "Plano cadastrado com sucesso!", newPlan);
    }

    // ================= BUSCA =================
    // Busca um plano pelo nome e retorna null se não encontrado
    public Plan findByName(String name) {
        for (Plan current : plans) {
            if (current.getName().equalsIgnoreCase(name)) {
                return current;
            }
        }
        return null;
    }


    // ================= ATUALIZAÇÃO =================

    // Atualiza o preço mensal de um plano existente
    // Importante: matrículas antigas não são afetadas

    public OperationResult updatePrice(String name, String priceStr) {

        // Busca plano
        Plan planUpdate = findByName(name);

        if (planUpdate == null) {
            return new OperationResult(false, "O plano não foi encontrado.");
        }

        // Converte e valida novo preço
        double newPrice = parseDouble(priceStr);
        if (newPrice <= 0) {
            return new OperationResult(false, "O preço deve ser positivo.");
        }

        // Atualiza preço no objeto
        planUpdate.updatePrice(newPrice);

        return new OperationResult(true, "O preço do plano " + name + " foi atualizado para R$ " + newPrice);
    }


    // ================= LISTAGEM =================
    // Retorna uma cópia da lista de planos cadastrados.
    // Uma cópia é retornada para impedir que classes externas
    // modifiquem a coleção interna diretamente.
    public ArrayList<Plan> listPlans() {
        return new ArrayList<>(plans);
    }


    // ================= PRIVADOS =================

    // Converte String para int com validação básica
    private int parseInt(String input) {
        if (input == null || input.isBlank()) return -1;
        if (!input.matches("\\d+")) return -1;
        return Integer.parseInt(input);
    }

    // Converte String para double com validação básica
    private double parseDouble(String input) {
        if (input == null || input.isBlank()) return -1;
        if (!input.matches("\\d+(\\.\\d+)?")) return -1;
        return Double.parseDouble(input);
    }

}
