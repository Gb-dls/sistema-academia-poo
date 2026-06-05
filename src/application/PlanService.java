package application;

import domain.plan.*;
import java.util.ArrayList;
import java.util.Comparator;

// Classe responsável pela lógica de negócio dos planos
// Aqui ficam regras de cadastro, busca, atualização e validação de planos
public class PlanService extends Repository<Plan> {



    // ================= VALIDAÇÕES =================

    // Verifica se já existe um plano com o nome informado.
    // A comparação ignora maiúsculas/minúsculas para evitar duplicatas como
    // "Mensal" e "mensal".
    public boolean nameExists(String name){
        for (Plan current : items) {
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
    public OperationResult<Plan> registerPlan(String name, String description, String typeStr, String minDurationStr, String priceStr) {

        // Validação do tipo de plano
        if (typeStr == null) {
            return new OperationResult<>(false, "Tipo de plano inválido.");
        }

        // Validação do nome
        if (name == null || name.isBlank()) {
            return new OperationResult<>(false, "O nome do plano não pode ser vazio.");
        }

        // Validação da descrição
        if (description == null || description.isBlank()) {
            return new OperationResult<>(false, "A descrição não pode ser vazia.");
        }

        // Evita duplicação de planos
        if (nameExists(name)) {
            return new OperationResult<>(false, "Já há um plano com esse nome.");
        }

        // Converte e valida duração mínima
        int minDurationMonths = parseInt(minDurationStr);
        if (minDurationMonths <= 0) {
            return new OperationResult<>(false, "A duração mínima deve ser maior que zero.");
        }

        // Converte e valida preço
        double pricePerMonth = parseDouble(priceStr);
        if (pricePerMonth <= 0) {
            return new OperationResult<>(false, "O preço deve ser positivo.");
        }

        int type = parseInt(typeStr);
        Plan newPlan;
        // A decisão é baseada exclusivamente na escolha do usuário no menu (typeStr)
        if (type == 1) {
            newPlan = new MonthlyPlan(name, description, minDurationMonths, pricePerMonth);
        } else if (type == 2) {
            newPlan = new QuarterlyPlan(name, description, minDurationMonths, pricePerMonth);
        } else if (type == 3) {
            newPlan = new SemiAnnualPlan(name, description, minDurationMonths, pricePerMonth);
        } else if (type == 4) {
            newPlan = new AnnualPlan(name, description, minDurationMonths, pricePerMonth);
        } else {
            return new OperationResult<>(false, "Opção de tipo de plano inválida.");
        }

        // Adicionando plano a lista de forma ordenada
        items.add(newPlan);
        items.sort(Comparator.comparing(Plan::getName));

        return new OperationResult<>(true, "Plano " + name + " cadastrado com sucesso!", newPlan);

    }

    // ================= BUSCA =================
    // Busca um plano pelo nome e retorna null se não encontrado
    public OperationResult<Plan> findByName(String name) {
        for (Plan current : items) {
            if (current.getName().equalsIgnoreCase(name)) {
                return new OperationResult<>(true, "Plano encontrado.", current);
            }
        }

        return new OperationResult<>(false, "Plano não encontrado.");
    }

    // ================= ATUALIZAÇÃO =================

    // Atualiza o preço mensal de um plano existente
    // Importante: matrículas antigas não são afetadas

    public OperationResult<Plan> updatePrice(String name, String priceStr) {

        // Busca plano
        OperationResult<Plan> result = findByName(name);

        if (!result.isSuccess()) {
            return new OperationResult<>(false, "O plano não foi encontrado.");
        }

        Plan planUpdate = result.getData();

        // Converte e valida novo preço
        double newPrice = parseDouble(priceStr);
        if (newPrice <= 0) {
            return new OperationResult<>(false, "O preço deve ser positivo.");
        }

        // Atualiza preço no objeto
        planUpdate.updatePrice(newPrice);

        return new OperationResult<>(true, "O preço do plano " + name + " foi atualizado para R$ " + newPrice);
    }


    // ================= LISTAGEM =================
    // Retorna uma cópia da lista de planos cadastrados.
    // Uma cópia é retornada para impedir que classes externas
    // modifiquem a coleção interna diretamente.
    public OperationResult<ArrayList<Plan>> listPlans() {
        if(count() == 0){
            return new OperationResult<>(false, "Nenhum plano cadastrado.");
        }

        return new OperationResult<>(true, "Lista de planos carregada.", listAll());
    }

    // metodos concretos de repository implementar quando for inserir os arquivos
    @Override
    public void save(String filePath) {

    }

    @Override
    public void load(String filePath) {

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
