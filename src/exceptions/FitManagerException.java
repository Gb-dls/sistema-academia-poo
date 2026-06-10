package exceptions;

//Exception
public class FitManagerException extends RuntimeException  {
    // Construtor 1 //
    public FitManagerException(String message) {
        super(message);
    }

    // Construtor 2 //
    public FitManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
