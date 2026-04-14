package application;

import domain.Student;
import domain.Plan;
import domain.PlanType;
import domain.Enrollment;
import domain.PaymentType;
import application.StudentService;
import application.PlanService;
import application.EnrollmentService;
import application.OperationResult;
import java.time.LocalDate;
import java.util.ArrayList;



// Classe que faz a ponte entre a UI e os serviços.
public class FitManager {


    // ================= ATRIBUTOS =================

    // Serviço responsável pelas regras de negócio dos alunos
    private final StudentService studentService;

    // Serviço responsável pelas regras de negócio dos planos
    private final PlanService planService;

    // Serviço responsável pelas regras de negócio das matrículas
    private final EnrollmentService enrollmentService;

    // ================= CONSTRUTOR =================

    // Inicializa os dois serviços o FitManager é quem cria os serviços
    public FitManager() {
        this.studentService = new StudentService();
        this.planService = new PlanService();
        this.enrollmentService = new EnrollmentService();
    }



    // ================= ALUNOS =================
    // Cadastra um novo aluno
    // Recebe strings puras da UI, limpa CPF e contato, converte a data e repassa ao serviço
    public OperationResult registerStudent(String name, String cpf, String contact, String email, String birthDateStr) {
        String cleanCpf     = cpf.replaceAll("\\D", "");            // Remove qualquer caractere não numérico do CPF
        String cleanContact = contact.replaceAll("\\D", "");         // Remove qualquer caractere não numérico do contato

        // Converte a string da data para LocalDate e retorna null se o formato for invalido
        LocalDate birthDate = parseDate(birthDateStr);
        if (birthDate == null) {
            return new OperationResult(false, "\nData de nascimento inválida. Use o formato yyyy-MM-dd.\n");
        }

        // Monta o objeto Student com os dados já limpos e convertidos
        Student student = new Student(name, cleanCpf, cleanContact, email, birthDate);
        return studentService.registerStudent(student);      // Repassa ao serviço que vai validar e cadastrar
    }

    // Busca um aluno pelo CPF e limpa o CPF antes de buscar
    public OperationResult findStudentByCpf(String cpf) {
        String cleanCpf = cpf.replaceAll("\\D", "");
        return studentService.findByCpf(cleanCpf);
    }


    //Atualiza os dados de um aluno existente
    public OperationResult updateStudent(String cpf, String name, String contact, String email, String birthDateStr) {
        String cleanCpf     = cpf.replaceAll("\\D", "");
        String cleanContact = contact.replaceAll("\\D", "");

        LocalDate birthDate = parseDate(birthDateStr);
        if (birthDate == null) {
            return new OperationResult(false, "\nData de nascimento inválida. Use o formato yyyy-MM-dd.\n");
        }

        return studentService.updateStudent(cleanCpf, name, cleanContact, email, birthDate);
    }

    // Inativa um aluno (sem remover da lista) e limpa o CPF para aceitar qualquer formato
    public OperationResult deleteStudent(String cpf) {
        String cleanCpf = cpf.replaceAll("\\D", "");
        return studentService.deactivateStudent(cleanCpf);
    }

    // Retorna a lista de todos os alunos cadastrados
    public OperationResult listStudents() {
        return studentService.listStudents();
    }



    // ================= PLANOS =================

    // Cadastra um novo plano repassando os dados diretamente ao serviço
    public OperationResult registerPlan(String name, String description, PlanType type, int minDurationMonths, double pricePerMonth) {
        return planService.registerPlan(name, description, type, minDurationMonths, pricePerMonth);
    }

    // Busca um plano pelo nome
    public OperationResult findPlanByName(String name) {
        Plan plan = planService.findByName(name);
        if (plan == null) {
            return new OperationResult(false, "Plano não encontrado.");
        }
        return new OperationResult(true, "Plano encontrado.", plan);
    }

    // Atualiza o preço mensal de um plano existente
    public OperationResult updatePlanPrice(String name, double newPrice) {
        return planService.updatePrice(name, newPrice);
    }

    // Retorna a lista de todos os planos cadastrados
    public ArrayList<Plan> listPlans() {
        return planService.listPlans();
    }




    // ================= MATRICULAS=================


    // Realiza a matrícula de um aluno em um plano
    public OperationResult enroll(Student student, Plan plan, LocalDate startDate, int durationMonths, double initialPayment, PaymentType paymentType) {
        return enrollmentService.enroll(student, plan, startDate, durationMonths, initialPayment, paymentType);
    }

    // Registra um pagamento em uma matrícula existente
    public OperationResult registerPayment(int enrollmentCode, double amount, PaymentType type, String description) {
        return enrollmentService.registerPayment(enrollmentCode, amount, type, description);
    }

    // Cancela uma matrícula pelo código
    public OperationResult cancelEnrollment(int code) {
        return enrollmentService.cancel(code);
    }

    // Consulta a matrícula ativa de um aluno pelo CPF
    public OperationResult findActiveEnrollmentByStudent(String cpf) {
        String cleanCpf = cpf.replaceAll("\\D", "");
        Enrollment enrollment = enrollmentService.findActiveByStudent(cleanCpf);
        if (enrollment == null) {
            return new OperationResult(false, "Nenhuma matrícula ativa encontrada para o CPF informado.");
        }
        return new OperationResult(true, "Matrícula ativa encontrada.", enrollment);
    }

    // Retorna a lista de todas as matrículas (histórico)
    public ArrayList<Enrollment> listEnrollments() {
        return enrollmentService.listEnrollments();
    }


    // ================= PRIVADOS =================

    // Converte uma string no formato "yyyy-MM-dd"
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;

        if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) return null;

        int year  = Integer.parseInt(dateStr.substring(0, 4));
        int month = Integer.parseInt(dateStr.substring(5, 7));
        int day   = Integer.parseInt(dateStr.substring(8, 10));

        if (month < 1 || month > 12) return null;
        if (day < 1 || day > 31)     return null;

        return LocalDate.of(year, month, day);
    }







}