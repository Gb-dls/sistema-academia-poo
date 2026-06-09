package application;

import domain.plan.*;
import exceptions.*;
import java.util.ArrayList;
import java.util.Comparator;

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
    // ou lança exceções descritivas caso alguma validação não passe.

    // ================= CADASTRAR PLANO =================
    // Registra um novo plano no sistema
    public OperationResult<Plan> registerPlan(String name, String description, String typeStr, String minDurationStr, String priceStr) throws ValidationException, BusinessException {

        // Validação do tipo de plano
        if (typeStr == null || typeStr.isBlank()) {
            throw new RequiredFieldException("Tipo de plano");
        }

        // Validação do nome
        if (name == null || name.isBlank()) {
            throw new RequiredFieldException("Nome do plano");
        }

        // Validação da descrição
        if (description == null || description.isBlank()) {
            throw new RequiredFieldException("Descrição");
        }

        // Evita duplicação de planos
        if (nameExists(name)) {
            throw new DuplicatedPlanException(name);
        }

        // Converte e valida duração mínima
        int minDurationMonths = parseInt(minDurationStr);
        if (minDurationMonths <= 0) {
            throw new InvalidFormatFieldException("Duração", "Número inteiro maior que zero");
        }

        // Converte e valida preço
        double pricePerMonth = parseDouble(priceStr);
        if (pricePerMonth <= 0) {
            throw new InvalidFormatFieldException("Preço", "Valor numérico positivo");
        }

        int type = parseInt(typeStr);
        Plan newPlan;

        // A decisão é baseada na escolha do usuário no menu
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

        // Adicionando plano a lista de forma ordenada
        plans.add(newPlan);
        plans.sort(Comparator.comparing(Plan::getName));

        return new OperationResult<>(true, "Plano " + name + " cadastrado com sucesso!", newPlan);
    }

    // ================= BUSCA =================
    // Busca um plano pelo nome e retorna null se não encontrado
    public OperationResult<Plan> findByName(String name) {
        for (Plan current : plans) {
            if (current.getName().equalsIgnoreCase(name)) {
                return new OperationResult<>(true, "Plano encontrado.", current);
            }
        }

        return new OperationResult<>(false, "Plano não encontrado.");
    }

    // ================= ATUALIZAÇÃO =================

    // Atualiza o preço mensal de um plano existente
    // Importante: matrículas antigas não são afetadas
    public OperationResult<Plan> updatePrice(String name, String priceStr) throws ValidationException, BusinessException {

        // Busca plano
        OperationResult<Plan> result = findByName(name);

        if (!result.isSuccess()) {
            throw new BusinessException("O plano informado não foi encontrado no sistema.");
        }

        Plan planUpdate = result.getData();

        // Converte e valida novo preço
        double newPrice = parseDouble(priceStr);
        if (newPrice <= 0) {
            throw new InvalidFormatFieldException("Novo preço", "Valor numérico positivo");
        }

        // Atualiza preço no objeto
        planUpdate.updatePrice(newPrice);

        return new OperationResult<>(true, "O preço do plano " + name + " foi atualizado para R$ " + newPrice, planUpdate);
    }

    // ================= LISTAGEM =================
    // Retorna uma cópia da lista de planos cadastrados.
    public OperationResult<ArrayList<Plan>> listPlans() {
        if (plans.isEmpty()) {
            return new OperationResult<>(false, "Nenhum plano cadastrado.");
        }
        return new OperationResult<>(true, "Lista de planos carregada.", new ArrayList<>(plans));
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