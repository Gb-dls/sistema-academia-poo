package exceptions;

public class DuplicatedPlanException extends BusinessException {
    public DuplicatedPlanException(String planName) {
        super("Não foi possível cadastrar: Já existe um plano com o nome '" + planName + "'.");
    }
}
