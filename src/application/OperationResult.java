package application;

public class OperationResult {
    private boolean success;
    private String message;
    private Object data; /*Permite retornar um objeto junto com o resultado. Será substituído por tipo genérico T nas etapas seguintes.*/

    public OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public OperationResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess(){
        return this.success;
    }

    public String getMessage(){
        return this.message;
    }

    public Object getData(){
        return this.data;
    }
}
