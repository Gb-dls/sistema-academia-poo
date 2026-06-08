package exceptions;

public class InvalidFormatFieldException extends ValidationException {
    public InvalidFormatFieldException(String fieldName, String expectedFormat) {
        super("O campo '" + fieldName + "' está inválido. Formato esperado: " + expectedFormat);
    }
}
