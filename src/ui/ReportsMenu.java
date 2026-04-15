package ui;



import application.FitManager;
import ui.UserInterface;
import application.OperationResult;
import java.util.ArrayList;
import domain.Student;
import domain.Enrollment;
import domain.EnrollmentStatus;
import domain.Plan;


// Classe responsável pelo menu de gerenciamento dos relatorios
public class ReportsMenu {

    // Interface responsável pela comunicação com o usuário (entrada/saída)
    private final UserInterface ui;

    // Classe principal de regras de negócio do sistema
    private final FitManager fitManager;

    // Construtor: recebe as dependências necessárias para o menu funcionar
    public ReportsMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }

    // Método principal do menu de alunos
    public void start() {

        String option;

        // Loop que mantém o menu ativo até o usuário escolher sair
        do {
            ui.showMenu(
                    "RELATÓRIOS",
                    """
                    1 - Alunos com matrícula ativa
                    2 - Matrículas com saldo pendente
                    3 - Todas as matrículas
                    4 - Todos os planos
                    5 - Consultar aluno por CPF
                    6 - Consultar plano por nome
                    7 - Matrícula ativa de um aluno por CPF
                    8 - Voltar
                    """
            );

            option = ui.getInput("");       // Le a opção do usuário

            switch (option) {

                case "1" -> listActiveStudents();

                case "2" -> listPendingEnrollments();

                case "3" -> listAllEnrollments();

                case "4" -> listAllPlans();

                case "5" -> findStudentByCpf();

                case "6" -> findPlanByName();

                case "7" -> findActiveEnrollmentByCpf();

                case "8" -> ui.showMessage("Voltando ao menu principal...");

                default -> ui.showError("Opção inválida!");
            }

        } while (!option.equals("8"));
    }


    // Lista todos os alunos que possuem matrícula ativa
    private void listActiveStudents() {

        ArrayList<Student> activeStudents = fitManager.listActiveStudents();

        if (activeStudents.isEmpty()) {
            ui.showError("Nenhum aluno com matrícula ativa encontrado.");
            return;
        }

        ui.showMessage("===== ALUNOS COM MATRÍCULA ATIVA =====");

        for (int i = 0; i < activeStudents.size(); i++) {
            Student s = activeStudents.get(i);
            ui.showMessage("Nome: " + s.getName() + " | CPF: " + s.getCpf());
        }

        ui.showMessage("Total: " + activeStudents.size() + " aluno(s).");
        ui.showMessage("======================================");
    }


    // Lista matrículas que possuem saldo pendente
    private void listPendingEnrollments() {

        OperationResult result = fitManager.listPendingEnrollments();

        if (!result.isSuccess()) {
            ui.showMessage("ERRO: " + result.getMessage());
            return;
        }

        ui.showMessage(result.getMessage());
    }


    // Lista todas as matrículas cadastradas no sistema
    private void listAllEnrollments() {

        ArrayList<Enrollment> enrollments = fitManager.listEnrollments();

        if (enrollments.isEmpty()) {
            ui.showMessage("ERRO: Nenhuma matrícula cadastrada.");
            return;
        }

        ui.showMessage("===== TODAS AS MATRÍCULAS =====");

        for (int i = 0; i < enrollments.size(); i++) {

            Enrollment e = enrollments.get(i);

            ui.showMessage(e.toString());

        }

        ui.showMessage("Total: " + enrollments.size());
        ui.showMessage("================================");
    }

    // Lista todos os planos disponíveis no sistema
    private void listAllPlans() {

        ArrayList<Plan> plans = fitManager.listPlans();

        if (plans.isEmpty()) {
            ui.showMessage("ERRO: Nenhum plano cadastrado.");
            return;
        }

        ui.showMessage("===== TODOS OS PLANOS =====");

        for (int i = 0; i < plans.size(); i++) {

            Plan p = plans.get(i);

            ui.showMessage(plans.get(i).toString());
        }

        ui.showMessage("Total: " + plans.size());
        ui.showMessage("============================");
    }



    // Busca um aluno pelo CPF informado pelo usuário
    private void findStudentByCpf() {

        String cpf = ui.getInput("Digite o CPF do aluno:");

        OperationResult result = fitManager.findStudentByCpf(cpf);

        if (!result.isSuccess() || result.getData() == null) {
            ui.showMessage("ERRO: " + result.getMessage());
            return;
        }

        Student student = (Student) result.getData();

        ui.showMessage(student.toString());
    }

    // Busca um plano pelo nome informado pelo usuário
    private void findPlanByName() {

        String name = ui.getInput("Digite o nome do plano:");

        OperationResult result = fitManager.findPlanByName(name);

        if (!result.isSuccess() || result.getData() == null) {
            ui.showMessage("ERRO: " + result.getMessage());
            return;
        }

        Plan plan = (Plan) result.getData();

        ui.showMessage(plan.toString());
    }

    // Busca matrícula ativa de um aluno pelo CPF
    private void findActiveEnrollmentByCpf() {

        String cpf = ui.getInput("Digite o CPF do aluno:");

        OperationResult result = fitManager.findActiveEnrollmentByStudent(cpf);

        if (!result.isSuccess() || result.getData() == null) {
            ui.showMessage("ERRO: " + result.getMessage());
            return;
        }

        Enrollment enrollment = (Enrollment) result.getData();

        ui.showMessage(enrollment.toString());
    }


}