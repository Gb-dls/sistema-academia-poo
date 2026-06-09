package exceptions;

public class FitManagerException extends Exception {
    // Construtor 1 //
    public FitManagerException(String message) {
        super(message);
    }

    // Construtor 2 //
    public FitManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
