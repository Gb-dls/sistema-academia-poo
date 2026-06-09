package application;

import domain.Student;
import formatters.DateFormatter;
import validators.CpfValidator;
import validators.ContactValidator;
import exceptions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

// Classe responsável pela lógica de negócio dos alunos
// Aqui ficam as regras do sistema (validações, criação, atualização, busca)
public class StudentService extends Repository<Student> {

    // ================= ATRIBUTOS =================

    // Validador responsável por regras de CPF
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

        // Adiciona aluno na lista em memória
        items.add(student);

        // Mantém lista ordenada por nome (ordem alfabética)
        items.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));

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
        if (items.isEmpty()) {
            return new OperationResult<>(false, "Nenhum aluno cadastrado.");
        }
        // Retorna cópia da lista para evitar alteração externa
        return new OperationResult<>(true, "Lista de alunos carregada.", listAll());
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

        // Reordena lista após atualização
        items.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));

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

    // ================= MÉTODOS PRIVADOS =================
    private Student findEntityByCpf(String cpf) {
        for(int i = 0; i < items.size(); i++){
            if (items.get(i).getCpf().equals(cpf)) {
                return items.get(i);
            }
        }
        return null;
    }

    // metodos concretos de repository implementar quando for inserir os arquivos
    @Override
    public void save(String filePath) {

    }

    @Override
    public void load(String filePath) {

    }

    // Verifica se um CPF já está cadastrado
    private boolean cpfExists(String cpf) {
        return findEntityByCpf(cpf) != null;
    }
}