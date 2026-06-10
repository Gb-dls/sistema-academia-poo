package exceptions;

public class RequiredFieldException extends ValidationException {
    public RequiredFieldException(String fieldName) {
        super("O campo '" + fieldName + "' é obrigatório e deve ser preenchido.");
    }
}
