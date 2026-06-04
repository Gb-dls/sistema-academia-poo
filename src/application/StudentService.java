package application;

import domain.Student;
import application.OperationResult;
import validators.CpfValidator;
import validators.ContactValidator;
import domain.Enrollment;
import formatters.DateFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;


// Classe responsável pela lógica de negócio dos alunos
// Aqui ficam as regras do sistema (validações, criação, atualização, busca)
public class StudentService {

    // ================= ATRIBUTOS =================

    // Lista em memória que armazena todos os alunos cadastrados
    private List<Student> students = new ArrayList<>();

    // Validador responsável por regras de CPF
    private CpfValidator cpfValidator = new CpfValidator();

    // Validador responsável por regras de contato/telefone
    private ContactValidator contactValidator = new ContactValidator();

    // Serviço responsável por matrículas (para validações relacionadas a aluno)
    private EnrollmentService enrollmentService;

    // Construtor da dependência do EnrollmentService
    public StudentService(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }



    // ================= CADASTRAR ALUNO =================

    public OperationResult<Student>  registerStudent(String name, String cpf, String contact, String email, String birthDateStr) {
        // Converte string de data para LocalDate
        String cleanCpf     = DateFormatter.cleanNumber(cpf);
        String cleanContact = DateFormatter.cleanNumber(contact);
        LocalDate birthDate = DateFormatter.parseDate(birthDateStr);

        // Validações de regra de negócio
        if (name == null || name.isBlank()) {
            return new OperationResult<>(false, "Nome inválido!");
        }

        if (!cpfValidator.isValidCpf(cleanCpf)) {
            return new OperationResult<>(false, "CPF inválido.\n");
        }

        if (cpfExists(cleanCpf)) {
            return new OperationResult<>(false, "CPF já cadastrado.\n");
        }


        if (!contactValidator.isValidContact(cleanContact)) {
            return new OperationResult<>(false, "Telefone inválido!");
        }

        if (email == null || email.isBlank()) {
            return new OperationResult<>(false, "Email inválido!");
        }

        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            return new OperationResult<>(false, "Data de nascimento inválida!");
        }

        // Criação do objeto aluno após validações
        Student student = new Student(name, cleanCpf, cleanContact, email, birthDate);

        // Adiciona aluno na lista em memória
        students.add(student);

        // Mantém lista ordenada por nome (ordem alfabética)
        students.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER)); //ordena a lista por ordem alfabética

        // Cria uma cópia para não expor o objeto original
        Student copy = new Student(
                student.getName(),
                student.getCpf(),
                student.getContact(),
                student.getEmail(),
                student.getBirthDate()
        );

        return new OperationResult<>(true, "Aluno cadastrado com sucesso.\n", copy);
    }


    // ================= BUSCAR POR CPF =================
    public OperationResult<Student> findByCpf(String cpf) {

        // Busca entidade real na lista
        Student student = findEntityByCpf(cpf);

        // Retorna cópia do aluno encontrado
        if (student != null) {
            Student copy = new Student(
                    student.getName(),
                    student.getCpf(),
                    student.getContact(),
                    student.getEmail(),
                    student.getBirthDate()
            );
            return new OperationResult<>(true, "Aluno encontrado.\n", copy);
        }
        return new OperationResult<>(false, "Aluno não encontrado.\n");
    }

    // ================= LISTAR ALUNOS =================
    public OperationResult<ArrayList<Student>> listStudents() {

        // Verifica se não há alunos cadastrados
        if (students.isEmpty()) {
            return new OperationResult<>(false, "Nenhum aluno cadastrado.");
        }
        // Retorna cópia da lista para evitar alteração externa
        return new OperationResult<>(true, "Lista de alunos carregada.", new ArrayList<>(students));
    }

    // ================= ATUALIZAR ALUNO =================
    public OperationResult<Student> updateStudent(String cpf, String name, String contact, String email, String birthDateStr) {

        // Normaliza entrada
        String cleanCpf     = DateFormatter.cleanNumber(cpf);
        String cleanContact = DateFormatter.cleanNumber(contact);
        LocalDate birthDate = DateFormatter.parseDate(birthDateStr);

        // Busca aluno existente
        Student student = findEntityByCpf(cleanCpf);

        // Validações de atualização
        if (student == null) {
            return new OperationResult<>(false, "Aluno não encontrado.\n");
        }

        if (name == null || name.isBlank()) {
            return new OperationResult<>(false, "Nome inválido!");
        }

        if (!contactValidator.isValidContact(cleanContact)) {
            return new OperationResult<>(false, "Telefone inválido!");
        }

        if (email == null || email.isBlank()) {
            return new OperationResult<>(false, "Email inválido!");
        }

        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            return new OperationResult<>(false, "Data de nascimento inválida!");
        }

        // Atualiza dados do aluno existente
        student.setName(name);
        student.setContact(cleanContact);
        student.setEmail(email);
        student.setBirthDate(birthDate);

        // Reordena lista após atualização
        students.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));

        // Cria cópia para retorno com os dados do aluno atualizados
        Student copy = new Student(
                student.getName(),
                student.getCpf(),
                student.getContact(),
                student.getEmail(),
                student.getBirthDate()
        );

        return new OperationResult<>(true, "Aluno atualizado com sucesso.\n", copy);
    }



    // ================= INATIVAR ALUNO =================
    public OperationResult<Void> removeStudent(String cpf) {

        String cleanCpf = DateFormatter.cleanNumber(cpf);
        // Busca aluno
        Student student = findEntityByCpf(cleanCpf);

        if (student == null) {
            return new OperationResult<>(false, "Aluno não encontrado.");
        }

        // Verifica se já está inativo
        if (!student.isActive()) {
            return new OperationResult<>(false, "Aluno já está inativo.");
        }

        // Verifica se possui matrícula ativa
        if (enrollmentService.hasActiveEnrollment(cleanCpf)) {
            return new OperationResult<>(false, "Aluno possui matrícula ativa, não pode ser inativado.");
        }

        // Verifica se possui débitos pendentes
        if (enrollmentService.hasDebt(cleanCpf)) {
            return new OperationResult<>(false, "Aluno possui débitos pendentes, não pode ser inativado.");
        }

        //Inativa o aluno
        student.deactivate();

        return new OperationResult<>(true, "Aluno inativado com sucesso.");
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