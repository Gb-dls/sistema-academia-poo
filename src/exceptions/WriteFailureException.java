package exceptions;

public class WriteFailureException extends PersistenceException {
    public WriteFailureException(String fileName, Throwable cause) {
        super("Falha crítica de E/S: Não foi possível gravar os dados no arquivo '" + fileName + "'.", cause);
    }
}
