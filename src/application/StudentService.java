package application;

import domain.Student;
import application.OperationResult;
import validators.CpfValidator;
import validators.ContactValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Classe responsável pela lógica de negócio dos alunos, recebe objetos Student já montados pelo FitManager e aplica as regras do sistema
public class StudentService {

    // ================= ATRIBUTOS =================

    // Lista que armazena todos os alunos cadastrados em memória
    private List<Student> students = new ArrayList<>();

    // Responsável por validar o CPF
    private CpfValidator cpfValidator = new CpfValidator();

    // ================= CADASTRAR ALUNO =================
    // Recebe um objeto Student já montado pelo FitManager, valida o CPF, verifica duplicidade efaz as outras validações
    public OperationResult registerStudent(Student student) {


        if (student.getName() == null || student.getName().isBlank()) {
            return new OperationResult(false, "Nome inválido!");
        }

        //Validação CPF
        if (!cpfValidator.isValidCpf(student.getCpf())) {
            return new OperationResult(false, "\nCPF inválido.\n");
        }

        // Duplicidade de CPF
        if (cpfExists(student.getCpf())) {
            return new OperationResult(false, "\nCPF já cadastrado.\n");
        }

        // Contato
        ContactValidator contactValidator = new ContactValidator();
        if (!contactValidator.isValidContact(student.getContact())) {
            return new OperationResult(false, "Telefone inválido!");
        }

        // Email
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            return new OperationResult(false, "Email inválido!");
        }

        // Data de nascimento
        if (student.getBirthDate() == null || student.getBirthDate().isAfter(LocalDate.now())) {
            return new OperationResult(false, "Data de nascimento inválida!");
        }

       //insere na lista
        students.add(student);


        Student copy = new Student(
                student.getName(),
                student.getCpf(),
                student.getContact(),
                student.getEmail(),
                student.getBirthDate()
        );

        return new OperationResult(true, "\nAluno cadastrado com sucesso.\n", copy);
    }

    // ================= BUSCAR POR CPF =================
    // Busca um aluno pelo CPF e retorna uma cópia do objeto
    public OperationResult findByCpf(String cpf) {

        Student student = findEntityByCpf(cpf);

        if (student != null) {
            Student copy = new Student(
                    student.getName(),
                    student.getCpf(),
                    student.getContact(),
                    student.getEmail(),
                    student.getBirthDate()
            );
            return new OperationResult(true, "\nAluno encontrado.\n", copy);
        }
        return new OperationResult(false, "\nAluno não encontrado.\n");
    }

    // ================= LISTAR ALUNOS =================
    // Retorna uma cópia da lista de alunos
    public OperationResult listStudents() {

        if (students.isEmpty()) {
            return new OperationResult(false, "Nenhum aluno cadastrado.");
        }

        return new OperationResult(true, "Lista de alunos carregada.", new ArrayList<>(students));
    }

    // ================= ATUALIZAR ALUNO =================

    public OperationResult updateStudent(String cpf, String name, String contact, String email, LocalDate birthDate) {

        Student student = findEntityByCpf(cpf);

        if (student == null) {
            return new OperationResult(false, "\nAluno não encontrado.\n");
        }

        // Nome
        if (name == null || name.isBlank()) {
            return new OperationResult(false, "Nome inválido!");
        }

        // Contato
        ContactValidator contactValidator = new ContactValidator();
        if (!contactValidator.isValidContact(contact)) {
            return new OperationResult(false, "Telefone inválido!");
        }

        // Email
        if (email == null || email.isBlank()) {
            return new OperationResult(false, "Email inválido!");
        }

        // Data de nascimento
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            return new OperationResult(false, "Data de nascimento inválida!");
        }


        student.setName(name);
        student.setContact(contact);
        student.setEmail(email);
        student.setBirthDate(birthDate);

        Student copy = new Student(
                student.getName(),
                student.getCpf(),
                student.getContact(),
                student.getEmail(),
                student.getBirthDate()
        );

        return new OperationResult(true, "\nAluno atualizado com sucesso.\n", copy);
    }


    // ================= REMOVER ALUNO =================
    // Remove o aluno definitivamente da lista
    public OperationResult removeStudent(String cpf) {

        Student student = findEntityByCpf(cpf);

        if (student == null) {
            return new OperationResult(false, "Aluno não encontrado.");
        }

        students.remove(student);
        return new OperationResult(true, "Aluno removido com sucesso.");
    }

    // ================= INATIVAR ALUNO =================
    // Marca o aluno como inativo sem removê-lo da lista
    public OperationResult deactivateStudent(String cpf) {

        Student student = findEntityByCpf(cpf);

        if (student == null) {
            return new OperationResult(false, "Aluno não encontrado.");
        }

        student.deactivate();
        return new OperationResult(true, "Aluno inativado com sucesso.");
    }

    // ================= MÉTODOS PRIVADOS =================

    // Percorre a lista procurando um aluno com o CPF informado
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