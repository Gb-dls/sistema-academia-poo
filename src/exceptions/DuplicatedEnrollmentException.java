package exceptions;

    public class DuplicatedEnrollmentException extends BusinessException {
    public DuplicatedEnrollmentException(int enrollmentId) {
        super("Falha de integridade: Já existe uma matricula com o código " + enrollmentId + ".");
    }
}
