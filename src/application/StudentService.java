package application;

import domain.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.OperationResult;

public class StudentService {

    private List<Student> students = new ArrayList<>();

    // cadastrar aluno
    public OperationResult registerStudent(Student student) {

        // verifica duplicidade de CPF
        if (cpfExists(student.getCpf())) {
            return new OperationResult(false, "CPF já cadastrado.");
        }

        students.add(student);
        return new OperationResult(true, "Aluno cadastrado com sucesso.", student);
    }

    // buscar por CPF
    public OperationResult findByCpf(String cpf) {

        Student student = findEntityByCpf(cpf);

        if (student != null) {
            return new OperationResult(true, "Aluno encontrado.", student);
        }

        return new OperationResult(false, "Aluno não encontrado.");
    }



    // listar alunos
    public OperationResult listStudents() {

        if (students.isEmpty()) {
            return new OperationResult(false, "Nenhum aluno cadastrado.");
        }

        return new OperationResult(true, "Lista de alunos.", students);
    }

    // editar aluno
    public OperationResult updateStudent(String cpf, String name, String contact, String email, LocalDate birthDate) {

        Student student = findEntityByCpf(cpf);

        if (student == null) {
            return new OperationResult(false, "Aluno não encontrado.");
        }

        try {
            student.setName(name);
            student.setContact(contact);
            student.setEmail(email);
            student.setBirthDate(birthDate);

            return new OperationResult(true, "Aluno atualizado com sucesso.", student);

        } catch (IllegalArgumentException e) {
            return new OperationResult(false, e.getMessage());
        }
    }


    // remover aluno
    public OperationResult removeStudent(String cpf) {

        Student student = findEntityByCpf(cpf);

        if (student == null) {
            return new OperationResult(false, "Aluno não encontrado.");
        }

        students.remove(student);
        return new OperationResult(true, "Aluno removido com sucesso.");
    }

    // inativar aluno
    public OperationResult deactivateStudent(String cpf) {

        Student student = findEntityByCpf(cpf);

        if (student == null) {
            return new OperationResult(false, "Aluno não encontrado.");
        }

        student.deactivate();
        return new OperationResult(true, "Aluno inativado com sucesso.");
    }

    // ================= MÉTODOS PRIVADOS =================

    private Student findEntityByCpf(String cpf) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getCpf().equals(cpf)) {
                return students.get(i);
            }
        }
        return null;
    }

    private boolean cpfExists(String cpf) {
        return findEntityByCpf(cpf) != null;
    }
}