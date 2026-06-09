package application;

import domain.Student;
import domain.plan.Plan;
import domain.Enrollment;
import domain.EnrollmentStatus;
import persistence.*;
import exceptions.ValidationException;
import exceptions.BusinessException;
import persistence.DataManager;
import ui.UserInterface;

import java.util.ArrayList;

// Classe que centraliza o acesso aos serviços e serve de ponte entre a UI e as regras de negócio
public class FitManager {

    // Serviço responsável pelas regras de negócio dos alunos
    private final StudentService studentService;
    // Serviço responsável pelas regras de negócio dos planos
    private final PlanService planService;
    // Serviço responsável pelas regras de negócio das matrículas
    private final EnrollmentService enrollmentService;


    // Repositorios //
    private final StudentRepository studentRepository;
    private final PlanRepository planRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final DataManager dataManager;

    // Inicializa e conecta todos os serviços do sistema
    public FitManager(UserInterface ui) {
        // repositories
        this.studentRepository = new StudentRepository();
        this.planRepository = new PlanRepository();
        this.enrollmentRepository = new EnrollmentRepository();

        // services
        this.enrollmentService = new EnrollmentService(enrollmentRepository);
        this.planService = new PlanService(planRepository);
        this.studentService = new StudentService(studentRepository, enrollmentService);
        this.dataManager = new DataManager(studentRepository, planRepository, enrollmentRepository, ui);

    }

    // ================= ALUNOS =================

    // Cadastra um aluno
    public OperationResult<Student> registerStudent(String name, String cpf, String contact, String email, String birthDateStr) throws ValidationException, BusinessException {
        return studentService.registerStudent(name, cpf, contact, email, birthDateStr);
    }

    // Busca aluno pelo CPF
    public OperationResult<Student> findStudentByCpf(String cpf) {
        return studentService.findByCpf(cpf);
    }

    // Atualiza dados de um aluno
    public OperationResult<Student> updateStudent(String cpf, String name, String contact, String email, String birthDateStr) throws ValidationException, BusinessException {
        return studentService.updateStudent(cpf, name, contact, email, birthDateStr);
    }

    // Inativa um aluno
    public OperationResult<Void> removeStudent(String cpf) throws BusinessException {
        return studentService.removeStudent(cpf);
    }

    // Lista todos os alunos cadastrados
    public OperationResult<ArrayList<Student>> listStudents() {
        return studentService.listStudents();
    }

    // ================= PLANOS =================

    // Cadastra um novo plano
    public OperationResult<Plan> registerPlan(String name, String description, String type, String minDuration, String price) throws ValidationException, BusinessException {
        return planService.registerPlan(name, description, type, minDuration, price);
    }

    // Busca plano pelo nome
    public OperationResult<Plan> findPlanByName(String name) {
        return planService.findByName(name);
    }

    // Atualiza o preço de um plano
    public OperationResult<Plan> updatePlanPrice(String name, String newPrice) throws ValidationException, BusinessException {
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
    public OperationResult<Enrollment> enroll(Student student, Plan plan, String startDateStr, String durationStr, String paymentStr, int paymentOption, String extra1, String extra2, String extra3) throws ValidationException, BusinessException {
        return enrollmentService.enroll(student, plan, startDateStr, durationStr, paymentStr, paymentOption, extra1, extra2, extra3);
    }

    /*
    @ registerPayment
    @ Objetivo: Repassar o registro do pagamento avulso com dados expandidos para o Service
    */
    public OperationResult<Enrollment> registerPayment(String codeStr, String amountStr, int paymentOption, String extra1, String extra2, String extra3) throws ValidationException, BusinessException {
        return enrollmentService.registerPayment(codeStr, amountStr, paymentOption, extra1, extra2, extra3);
    }

    // Cancela uma matrícula
    public OperationResult<Void> cancelEnrollment(String codeStr) throws ValidationException, BusinessException {
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
        ArrayList<Student> allStudents = studentService.listStudents().getData();
        ArrayList<Student> activeStudents = new ArrayList<>();
        for (Student s : allStudents) {
            if (enrollmentService.hasActiveEnrollment(s.getCpf())) {
                boolean exists = false;
                for(Student a : activeStudents){
                    if(a.getCpf().equals(s.getCpf())){
                        exists = true;
                        break;
                    }
                }
                if(!exists){
                    activeStudents.add(s);
                }
            }
        }
        return activeStudents;
    }

    // Lista todos os alunos que possuem dívidas pendentes
    public ArrayList<Student> listStudentsWithDebt() {
        ArrayList<Student> result = new ArrayList<>();

        for (Student s : studentService.listStudents().getData()) {
            if (enrollmentService.hasDebt(s.getCpf())) {
                result.add(s);
            }
        }

        return result;
    }

    // Lista matrículas que possuem saldo pendente
    public OperationResult<ArrayList<Enrollment>> listPendingEnrollments() {
        ArrayList<Student> debtStudents = listStudentsWithDebt();
        ArrayList<Enrollment> all = enrollmentService.listEnrollments().getData();
        ArrayList<Enrollment> pending = new ArrayList<>();

        for(Enrollment e : all){
            for (Student s : debtStudents) {
                if (e.getStudent().getCpf().equals(s.getCpf()) && e.calculateBalance() > 0) {
                    pending.add(e);
                    break;
                }
            }
        }
        if(pending.isEmpty()){
            return new OperationResult<>(false, "Nenhuma matrícula pendente.");
        }
            return new OperationResult<>(true, "Matrículas pendentes encontradas.", pending);
    }


    // ================= PERSISTÊNCIA =================

    public void loadAll() {
        dataManager.loadAll();
    }

    public void saveAll() {
         dataManager.saveAll();
    }

    public boolean isSucessoUltimoSalvamento() {
        return dataManager.isSucessoUltimoSalvamento();
    }
}