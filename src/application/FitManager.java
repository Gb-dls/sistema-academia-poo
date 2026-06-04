package application;

import domain.Student;
import domain.plan.Plan;
import domain.Enrollment;
import domain.EnrollmentStatus;
import java.util.ArrayList;

// Classe que centraliza o acesso aos serviços e serve de ponte entre a UI e as regras de negócio
public class FitManager {

    // Serviço responsável pelas regras de negócio dos alunos
    private final StudentService studentService;

    // Serviço responsável pelas regras de negócio dos planos
    private final PlanService planService;

    // Serviço responsável pelas regras de negócio das matrículas
    private final EnrollmentService enrollmentService;

    // Construtor //

    // Inicializa e conecta todos os serviços do sistema
    public FitManager() {
        this.enrollmentService = new EnrollmentService();
        this.studentService = new StudentService(enrollmentService);
        this.planService = new PlanService();
    }

    // ================= ALUNOS =================

    // Cadastra um aluno
    public OperationResult<Student> registerStudent(String name, String cpf, String contact, String email, String birthDateStr) {
        return studentService.registerStudent(name, cpf, contact, email, birthDateStr);
    }

    // Busca aluno pelo CPF
    public OperationResult<Student> findStudentByCpf(String cpf) {
        return studentService.findByCpf(cpf);
    }

    // Atualiza dados de um aluno
    public OperationResult<Student> updateStudent(String cpf, String name, String contact, String email, String birthDateStr) {
        return studentService.updateStudent(cpf, name, contact, email, birthDateStr);
    }

    // Inativa um aluno
    public OperationResult<Void> removeStudent(String cpf) {
        return studentService.removeStudent(cpf);
    }

    // Lista todos os alunos cadastrados
    public OperationResult<ArrayList<Student>>  listStudents() {
        return studentService.listStudents();
    }

    // ================= PLANOS =================

    // Cadastra um novo plano
    public  OperationResult<Plan> registerPlan(String name, String description, String type, String minDuration, String price) {
        return planService.registerPlan(name, description, type, minDuration, price);
    }

    // Busca plano pelo nome
    public OperationResult<Plan> findPlanByName(String name) {
        return planService.findByName(name);

    }

    // Atualiza o preço de um plano
    public OperationResult<Plan> updatePlanPrice(String name, String newPrice) {
        return planService.updatePrice(name, newPrice);
    }

    // Lista todos os planos
    public OperationResult<ArrayList<Plan>> listPlans() {
        return planService.listPlans();
    }

    // ================= MATRÍCULAS =================

    /*
    @ enroll
    @ Objetivo: Repassar a solicitação de matrícula com os parâmetros de pagamento expandidos para o Service
    */
    public OperationResult<Enrollment> enroll(Student student, Plan plan, String startDateStr, String durationStr, String paymentStr, int paymentOption, String extra1, String extra2, String extra3) {
        return enrollmentService.enroll(student, plan, startDateStr, durationStr, paymentStr, paymentOption, extra1, extra2, extra3);
    }

    /*
    @ registerPayment
    @ Objetivo: Repassar o registro do pagamento avulso com dados expandidos para o Service
    */
    public OperationResult<Enrollment> registerPayment(String codeStr, String amountStr, int paymentOption, String extra1, String extra2, String extra3) {
        return enrollmentService.registerPayment(codeStr, amountStr, paymentOption, extra1, extra2, extra3);
    }

    // Cancela uma matrícula
    public OperationResult<Void> cancelEnrollment(String codeStr) {
        return enrollmentService.cancel(codeStr);
    }

    // Consulta a matrícula ativa de um aluno pelo CPF
    public OperationResult<Enrollment> findActiveEnrollmentByStudent(String cpf) {
        Enrollment enrollment = enrollmentService.findActiveByStudent(cpf);
        if (enrollment == null) {
            return new OperationResult<>(false, "Nenhuma matrícula ativa encontrada para o CPF informado.");
        }
        return new OperationResult<>(true, "Matrícula ativa encontrada.", enrollment);
    }

    // Retorna a lista de todas as matrículas (histórico)
    public OperationResult<ArrayList<Enrollment>> listEnrollments() {
        return enrollmentService.listEnrollments();
    }

    // ================= RELATÓRIOS =================

    // Lista todos os alunos que possuem matrícula ativa no sistema
    public ArrayList<Student> listActiveStudents() {
        OperationResult<ArrayList<Enrollment>> result = listEnrollments();
        if(!result.isSuccess()){
            return new ArrayList<>();
        }

        ArrayList<Enrollment> enrollments = result.getData();
        ArrayList<Student> activeStudents = new ArrayList<>();

        for (int i = 0; i < enrollments.size(); i++) {
            Enrollment e = enrollments.get(i);

            if (e.getStatus() == EnrollmentStatus.ACTIVE)  {
                Student student = e.getStudent();
                boolean exists = false;

                for (int j = 0; j < activeStudents.size(); j++) {
                    if (activeStudents.get(j).getCpf().equals(student.getCpf())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    activeStudents.add(student);
                }
            }
        }
        return activeStudents;
    }

    // Lista todos os alunos que possuem dívidas pendentes
    public ArrayList<Student> listStudentsWithDebt() {
        OperationResult<ArrayList<Student>> result = studentService.listStudents();

        if (!result.isSuccess() || result.getData() == null) {
            return new ArrayList<>();
        }

        ArrayList<Student> students = result.getData();
        ArrayList<Student> withDebt = new ArrayList<>();

        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            if (s != null && enrollmentService.hasDebt(s.getCpf())) {
                withDebt.add(s);
            }
        }
        return withDebt;
    }

    // Lista matrículas que possuem saldo pendente
    public OperationResult<ArrayList<Enrollment>> listPendingEnrollments() {

        ArrayList<Student> studentsWithDebt = listStudentsWithDebt();

        if(studentsWithDebt.isEmpty()){
            return new OperationResult<>(false, "Nenhum aluno com dívida encontrado.");
        }

        OperationResult<ArrayList<Enrollment>> result = listEnrollments();

        if(!result.isSuccess() || result.getData() == null){
            return new OperationResult<>(false, "Nenhuma matrícula cadastrada.");
        }

        ArrayList<Enrollment> enrollments = result.getData();

        ArrayList<Enrollment> pending = new ArrayList<>();

        for(int i = 0; i < enrollments.size(); i++){
            Enrollment e = enrollments.get(i);

            if(e != null && e.getStudent() != null && e.getStatus() == EnrollmentStatus.ACTIVE){
                for(int j = 0; j < studentsWithDebt.size(); j++){
                    Student s = studentsWithDebt.get(j);
                    if(s != null && e.getStudent().getCpf().equals(s.getCpf()) && e.calculateBalance() > 0){
                        pending.add(e);
                        break;
                    }
                }
            }
        }

        if(pending.isEmpty()){
            return new OperationResult<>(false, "Nenhuma matrícula ativa com saldo pendente encontrada.");
        }
        return new OperationResult<>(true, "Matrículas pendentes encontradas.", pending);
    }
}