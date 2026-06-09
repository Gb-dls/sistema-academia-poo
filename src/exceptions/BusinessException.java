package exceptions;

/*
    Agrupa violações de regras de negócio do domínio.
*/
public class BusinessException extends FitManagerException {
    public BusinessException(String message) {
        super(message);
    }
}
