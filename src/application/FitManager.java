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



// Classe que funciona como FACHADA do sistema (Facade Pattern)
// Ela centraliza o acesso aos serviços e serve de ponte entre UI e regras de negócio
public class FitManager {


    // ================= ATRIBUTOS ================

    // Serviço responsável pelas regras de negócio dos alunos
    private final StudentService studentService;

    // Serviço responsável pelas regras de negócio dos planos
    private final PlanService planService;

    // Serviço responsável pelas regras de negócio das matrículas
    private final EnrollmentService enrollmentService;



    // ================= CONSTRUTOR =================

    // Inicializa os dois serviços o FitManager é responsável por instanciar e conectar os serviços
    public FitManager() {
        this.enrollmentService = new EnrollmentService();
        this.studentService = new StudentService(enrollmentService);
        this.planService = new PlanService();
    }



    // ================= ALUNOS =================

    // Cadastra um aluno
    public OperationResult registerStudent(String name, String cpf, String contact, String email, String birthDateStr) {
        return studentService.registerStudent(name, cpf, contact, email, birthDateStr);
    }

    // Busca aluno pelo CPF
    public OperationResult findStudentByCpf(String cpf) {
        return studentService.findByCpf(cpf);
    }

    // Atualiza dados de um aluno
    public OperationResult updateStudent(String cpf, String name, String contact, String email, String birthDateStr) {
        return studentService.updateStudent(cpf, name, contact, email, birthDateStr);
    }

    // Inativa um aluno
    public OperationResult removeStudent(String cpf) {
        return studentService.removeStudent(cpf);
    }

    // Lista todos os alunos cadastrados
    public OperationResult listStudents() {
        return studentService.listStudents();
    }


    // ================= PLANOS =================

    // Cadastra um novo plano
    public OperationResult registerPlan(String name, String description, PlanType type, String minDuration, String price) {
        return planService.registerPlan(name, description, type, minDuration, price);
    }

    // Busca  plano pelo nome
    public OperationResult findPlanByName(String name) {
        Plan plan = planService.findByName(name);
        if (plan == null) {
            return new OperationResult(false, "Plano não encontrado.");
        }
        return new OperationResult(true, "Plano encontrado.", plan);
    }

    // Atualiza o preço de um plano
    public OperationResult updatePlanPrice(String name, String newPrice) {
        return planService.updatePrice(name, newPrice);
    }

    // Lista todos os planos
    public ArrayList<Plan> listPlans() {
        return planService.listPlans();
    }



    // ================= MATRICULAS=================


    // Realiza a matrícula de um aluno em um plano
    public OperationResult enroll(Student student, Plan plan, String startDateStr, String durationStr, String paymentStr, PaymentType paymentType) {
        return enrollmentService.enroll(student, plan, startDateStr, durationStr, paymentStr, paymentType);
    }

    // Registra um pagamento em uma matrícula existente
    public OperationResult registerPayment(String codeStr, String amountStr, PaymentType type, String description) {
        return enrollmentService.registerPayment(codeStr, amountStr, type, description);
    }

    // Cancela uma matrícula
    public OperationResult cancelEnrollment(String codeStr) {
        return enrollmentService.cancel(codeStr);
    }


    // Consulta a matrícula ativa de um aluno pelo CPF
    public OperationResult findActiveEnrollmentByStudent(String cpf) {

        Enrollment enrollment = enrollmentService.findActiveByStudent(cpf);

        if (enrollment == null) {
            return new OperationResult(false, "Nenhuma matrícula ativa encontrada para o CPF informado.");
        }
        return new OperationResult(true, "Matrícula ativa encontrada.", enrollment);
    }

    // Retorna a lista de todas as matrículas (histórico)
    public ArrayList<Enrollment> listEnrollments() {
        return enrollmentService.listEnrollments();
    }



    // ================= RELATORIOS =================

    // Lista todos os alunos que possuem matrícula ativa no sistema
    public ArrayList<Student> listActiveStudents() {

        // Busca todas as matrículas cadastradas
        ArrayList<Enrollment> enrollments = listEnrollments();

        // Lista que vai armazenar alunos ativos
        ArrayList<Student> activeStudents = new ArrayList<>();

        // Percorre todas as matrículas
        for (int i = 0; i < enrollments.size(); i++) {

            Enrollment e = enrollments.get(i);

            // Verifica se a matrícula está ativa
            if (e.getStatus() == EnrollmentStatus.ACTIVE)  {

                // Pega o aluno da matrícula
                Student student = e.getStudent();

                boolean exists = false;     // evita duplicação de alunos na lista

                // Verifica se o aluno já foi adicionado
                for (int j = 0; j < activeStudents.size(); j++) {
                    if (activeStudents.get(j).getCpf().equals(student.getCpf())) {
                        exists = true;
                        break;
                    }
                }

                // Se ainda não existe na lista, adiciona
                if (!exists) {
                    activeStudents.add(student);
                }
            }
        }

        // Retorna lista final de alunos ativos
        return activeStudents;
    }



    // Lista todos os alunos que possuem dívida pendentes
    public ArrayList<Student> listStudentsWithDebt() {

        // Busca todos os alunos cadastrados no sistema
        ArrayList<Student> students = (ArrayList<Student>) studentService.listStudents().getData();

        // Lista que armazenará alunos com dívida
        ArrayList<Student> result = new ArrayList<>();

        // Se não houver alunos cadastrados, retorna lista vazia
        if (students == null || students.isEmpty()) {
            return result;
        }

        // Percorre todos os alunos
        for (int i = 0; i < students.size(); i++) {

            Student s = students.get(i);

            // Verifica se o aluno existe e se possui dívida
            if (s != null && enrollmentService.hasDebt(s.getCpf())) {
                result.add(s);
            }
        }

        // Retorna lista de alunos com pendencias
        return result;
    }

    // Lista matrículas que possuem saldo pendente
    public OperationResult listPendingEnrollments() {

        // Obtém alunos com dívida
        ArrayList<Student> studentsWithDebt = listStudentsWithDebt();

        if (studentsWithDebt.isEmpty()) {
            return new OperationResult(false, "Nenhum aluno com dívida encontrado.");
        }

        // Busca todas as matrículas
        ArrayList<Enrollment> enrollments = listEnrollments();

        if (enrollments == null || enrollments.isEmpty()) {   // caso não tenha matrículas cadastradas
            return new OperationResult(false, "Nenhuma matrícula cadastrada.");
        }

        // Lista que armazenará matrículas pendentes
        ArrayList<Enrollment> pending = new ArrayList<>();

        for (int i = 0; i < enrollments.size(); i++) {

            Enrollment e = enrollments.get(i);

            if (e == null || e.getStudent() == null) {
                continue;
            }

            // Verifica se essa matrícula pertence a um aluno com dívida
            for (int j = 0; j < studentsWithDebt.size(); j++) {

                Student s = studentsWithDebt.get(j);

                if (s == null) {
                    continue;
                }
                // Confere se é o mesmo aluno e se ainda há saldo pendente
                if (e.getStudent().getCpf().equals(s.getCpf())
                        && e.calculateBalance() > 0) {

                    pending.add(e);         // adiciona matrícula pendente
                    break;
                }
            }
        }

        // Se não encontrou nenhuma matrícula pendente
        if (pending.isEmpty()) {
            return new OperationResult(false, "Nenhuma matrícula com saldo pendente encontrada.");
        }

        // Monta o texto de saída formatado
        StringBuilder sb = new StringBuilder();
        sb.append("===== MATRÍCULAS PENDENTES =====\n\n");

        for (int i = 0; i < pending.size(); i++) {
            sb.append(pending.get(i)).append("\n\n");
        }

        return new OperationResult(true, sb.toString()); // // Retorna resultado de sucesso com a lista formatada
    }

}