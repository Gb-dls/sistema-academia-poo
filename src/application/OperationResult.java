package application;


// Classe que padroniza o retorno de todas as operações do sistema
public class OperationResult {
    // ================= ATRIBUTOS =================


    private boolean success;                // Indica se a operação foi bem-sucedida (true) ou falhou (false)
    private String message;                 // Mensagem descritiva do resultado
    private Object data;                    //Permite retornar um objeto junto com o resultado.


    // ================= CONSTRUTORES =================
    // Construtor sem objeto, usado quando a operação não precisa retornar dados
    public OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    // Construtor com objeto, usado quando a operação retorna um dado junto
    public OperationResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // ================= GETTERS =================
    // Retorna true se a operação foi bem-sucedida, false se falhou
    public boolean isSuccess(){
        return this.success;
    }

    // Retorna a mensagem descritiva do resultado
    public String getMessage(){
        return this.message;
    }

    // Retorna o objeto associado ao resultado, ou null se não houver
    public Object getData(){
        return this.data;
    }


}
