package exceptions;

public class StudentWithActiveRegistrationException extends BusinessException {
    public StudentWithActiveRegistrationException(String cpf) {
        super("Operação negada: O aluno com CPF " + cpf + " possui matrícula ativa no sistema.");
    }
}
