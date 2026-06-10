package exceptions;

public class PlanInUseException extends BusinessException {
    public PlanInUseException(String planName) {
        super("Não é possível excluir plano: '" + planName + "' possui matrículas ativas e não pode ser deletado.");
    }
}
