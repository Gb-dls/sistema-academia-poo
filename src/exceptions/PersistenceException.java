package exceptions;

/*
    Agrupa falhas de infraestrutura, leitura e escrita de arquivos.
*/
public class PersistenceException extends FitManagerException {
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
