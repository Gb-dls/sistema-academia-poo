package exceptions;

/*
    Agrupa erros de entradas mal formadas ou campos ausentes.
*/

public class ValidationException extends FitManagerException {
    public ValidationException(String message) {
        super(message);
    }
}
