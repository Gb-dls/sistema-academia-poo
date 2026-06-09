package exceptions;

public class DuplicatedStudentException extends BusinessException {
    public DuplicatedStudentException(String cpf) {
        super("Cadastro de aluno negado: O CPF " + cpf + " já está registrado no sistema.");
    }
}
