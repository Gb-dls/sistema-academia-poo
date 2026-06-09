package exceptions;

public class CorruptedFileException extends PersistenceException {
    public CorruptedFileException(String fileName, Throwable cause) {
        super("O arquivo '" + fileName + "' está corrompido ou em formato inválido.", cause);
    }
}
