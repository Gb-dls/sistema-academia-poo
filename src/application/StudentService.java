package application;

import domain.Student;
import formatters.DateFormatter;
import validators.CpfValidator;
import validators.ContactValidator;
import exceptions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class StudentService {

    private List<Student> students = new ArrayList<>();
    private CpfValidator cpfValidator = new CpfValidator();
    private ContactValidator contactValidator = new ContactValidator();
    private EnrollmentService enrollmentService;

    public StudentService(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // ================= CADASTRAR ALUNO =================
    public OperationResult<Student> registerStudent(String name, String cpf, String contact, String email, String birthDateStr) throws ValidationException, BusinessException {

        if (name == null || name.isBlank()) {
            throw new RequiredFieldException("Nome");
        }
        if (email == null || email.isBlank()) {
            throw new RequiredFieldException("E-mail");
        }

        String cleanCpf = DateFormatter.cleanNumber(cpf);
        if (!cpfValidator.isValidCpf(cleanCpf)) {
            throw new InvalidFormatFieldException("CPF", "11 dígitos numéricos válidos");
        }
        if (cpfExists(cleanCpf)) {
            throw new DuplicatedStudentException(cleanCpf);
        }

        String cleanContact = DateFormatter.cleanNumber(contact);
        if (!contactValidator.isValidContact(cleanContact)) {
            throw new InvalidFormatFieldException("Contato", "Formato de telefone válido");
        }

        LocalDate birthDate = DateFormatter.parseDate(birthDateStr);
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new InvalidFormatFieldException("Data de nascimento", "Uma data válida e anterior ao dia de hoje");
        }

        Student student = new Student(name, cleanCpf, cleanContact, email, birthDate);
        students.add(student);
        students.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));

        Student copy = new Student(student.getName(), student.getCpf(), student.getContact(), student.getEmail(), student.getBirthDate());
        return new OperationResult<>(true, "Aluno cadastrado com sucesso.", copy);
    }

    // ================= ATUALIZAR ALUNO =================
    public OperationResult<Student> updateStudent(String cpf, String name, String contact, String email, String birthDateStr) throws ValidationException, BusinessException {

        String cleanCpf = DateFormatter.cleanNumber(cpf);
        Student student = findEntityByCpf(cleanCpf);

        if (student == null) {
            throw new BusinessException("Aluno com o CPF informado não encontrado no sistema.");
        }

        if (name == null || name.isBlank()) throw new RequiredFieldException("Nome");
        if (email == null || email.isBlank()) throw new RequiredFieldException("E-mail");

        String cleanContact = DateFormatter.cleanNumber(contact);
        if (!contactValidator.isValidContact(cleanContact)) {
            throw new InvalidFormatFieldException("Contato", "Formato de telefone válido");
        }

        LocalDate birthDate = DateFormatter.parseDate(birthDateStr);
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new InvalidFormatFieldException("Data de nascimento", "Uma data válida e anterior ao dia de hoje");
        }

        student.setName(name);
        student.setContact(cleanContact);
        student.setEmail(email);
        student.setBirthDate(birthDate);

        students.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));

        Student copy = new Student(student.getName(), student.getCpf(), student.getContact(), student.getEmail(), student.getBirthDate());
        return new OperationResult<>(true, "Cadastro atualizado com sucesso.", copy);
    }

    // ================= INATIVAR ALUNO =================
    public OperationResult<Void> removeStudent(String cpf) throws BusinessException {

        String cleanCpf = DateFormatter.cleanNumber(cpf);
        Student student = findEntityByCpf(cleanCpf);

        if (student == null) {
            throw new BusinessException("Aluno não encontrado para exclusão.");
        }
        if (!student.isActive()) {
            throw new BusinessException("O aluno já consta como inativo no sistema.");
        }
        if (enrollmentService.hasActiveEnrollment(cleanCpf)) {
            throw new BusinessException("Bloqueado: O aluno possui uma matrícula ativa. Cancele a matrícula antes de inativá-lo.");
        }
        if (enrollmentService.hasDebt(cleanCpf)) {
            throw new BusinessException("Bloqueado: O aluno possui débitos pendentes com a academia.");
        }

        student.deactivate();
        return new OperationResult<>(true, "Aluno inativado com sucesso.");
    }

    // ================= BUSCAR E LISTAR (Apenas Consultas) =================
    public OperationResult<Student> findByCpf(String cpf) {
        Student student = findEntityByCpf(DateFormatter.cleanNumber(cpf));
        if (student != null) {
            Student copy = new Student(student.getName(), student.getCpf(), student.getContact(), student.getEmail(), student.getBirthDate());
            return new OperationResult<>(true, "Aluno encontrado.", copy);
        }
        return new OperationResult<>(false, "Aluno não encontrado no sistema.");
    }

    public OperationResult<ArrayList<Student>> listStudents() {
        if (students.isEmpty()) {
            return new OperationResult<>(false, "Nenhum aluno cadastrado.");
        }
        return new OperationResult<>(true, "Lista de alunos carregada.", new ArrayList<>(students));
    }

    // ================= MÉTODOS PRIVADOS =================
    private Student findEntityByCpf(String cpf) {
        for (Student s : students) {
            if (s.getCpf().equals(cpf)) return s;
        }
        return null;
    }

    private boolean cpfExists(String cpf) {
        return findEntityByCpf(cpf) != null;
    }
}