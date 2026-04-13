package application;

import domain.Student;
import application.OperationResult;
import validators.CpfValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


// Classe responsável pela lógica de negócio dos alunos
public class StudentService {

    // Lista que armazena os alunos em memória
    private List<Student> students = new ArrayList<>();

    // Validador de CPF
    private CpfValidator cpfValidator = new CpfValidator();


    // ================= CADASTRAR ALUNO =================
    public OperationResult registerStudent(Student student) {

        // valida CPF
        if (!cpfValidator.isValidCpf(student.getCpf())) {
            return new OperationResult(false, "\nCPF inválido.\n");
        }

        // verifica duplicidade de CPF
        if (cpfExists(student.getCpf())) {
            return new OperationResult(false, "\nCPF já cadastrado.\n");
        }

        //adiciona o aluno na lista
        students.add(student);

        // Retorna sucesso + dados do aluno
        return new OperationResult(true, "\nAluno cadastrado com sucesso.\n", student);
    }

    // ================= BUSCAR POR CPF =================
    public OperationResult findByCpf(String cpf) {

        // Busca o aluno internamente
        Student student = findEntityByCpf(cpf);

        if (student != null) {
            return new OperationResult(true, "\nAluno encontrado.\n", student); // Se encontrou, retorna sucesso + aluno
        }
        return new OperationResult(false, "\nAluno não encontrado.\n");     // Caso contrário, menssagem de erro
    }


    // ================= LISTAR ALUNOS =================
    public OperationResult listStudents() {

        // Se não tem alunos cadastrados
        if (students.isEmpty()) {
            return new OperationResult(false, "\nNenhum aluno cadastrado.\n");
        }
        // Retorna a lista completa
        return new OperationResult(true, "\nLista de alunos\n", students);
    }

    // ================= ATUALIZAR ALUNO =================
    public OperationResult updateStudent(String cpf, String name, String contact, String email, LocalDate birthDate) {

        // Busca o aluno
        Student student = findEntityByCpf(cpf);

        if (student == null) {
            return new OperationResult(false, "\nAluno não encontrado.\n");  // Se não encontrar, retorna menssagem de erro
        }

        try {
            student.setName(name);
            student.setContact(contact);
            student.setEmail(email);
            student.setBirthDate(birthDate);

            return new OperationResult(true, "\nAluno atualizado com sucesso.\n", student);

        } catch (IllegalArgumentException e) {
            return new OperationResult(false, e.getMessage());
        }
    }


    // ================= REMOVER ALUNO =================
    public OperationResult removeStudent(String cpf) {

        // Busca o aluno
        Student student = findEntityByCpf(cpf);

        if (student == null) {  // Se não encontrar retorna a menssagem de erro
            return new OperationResult(false, "Aluno não encontrado.");
        }

        students.remove(student); // Remove da lista
        return new OperationResult(true, "Aluno removido com sucesso.");
    }

    // ================= INATIVAR ALUNO =================
    public OperationResult deactivateStudent(String cpf) {

        // Busca o aluno
        Student student = findEntityByCpf(cpf);

        if (student == null) {      // Se não encontrar retorna a menssagem de erro
            return new OperationResult(false, "Aluno não encontrado.");
        }

        // Marca o aluno como inativo (sem remover da lista)
        student.deactivate();
        return new OperationResult(true, "Aluno inativado com sucesso.");
    }

    // ================= MÉTODOS PRIVADOS =================

    // Busca interna de aluno pelo CPF
    private Student findEntityByCpf(String cpf) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getCpf().equals(cpf)) {
                return students.get(i);
            }
        }
        return null;
    }

    // Verifica se um CPF já está cadastrado
    private boolean cpfExists(String cpf) {
        return findEntityByCpf(cpf) != null;
    }
}