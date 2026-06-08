package ui;



import application.FitManager;
import application.OperationResult;
import java.util.ArrayList;
import domain.Student;
import domain.Enrollment;
import domain.plan.Plan;


// Classe responsável pelo menu de gerenciamento dos relatorios
public class ReportsMenu {

    // 'Interface' responsável pela comunicação com o utilizador (entrada/saída)
    private final UserInterface ui;

    // Classe principal de regras de negócio do sistema
    private final FitManager fitManager;

    // Construtor: recebe as dependências necessárias para o menu funcionar
    public ReportsMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }

    // Metodo principal do menu de alunos
    public void start() {

        int option;

        // Looping que mantém o menu ativo até o utilizador escolher sair
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

            option = ui.getInt("");       // Le a opção do utilizador

            if (option == -1) {
                return;
            }

            switch (option) {

                case 1 -> listActiveStudents();

                case 2 -> listPendingEnrollments();

                case 3 -> listAllEnrollments();

                case 4 -> listAllPlans();

                case 5 -> findStudentByCpf();

                case 6 -> findPlanByName();

                case 7 -> findActiveEnrollmentByCpf();

                case 8 -> ui.showMessage("Voltando ao menu principal...");

                default -> ui.showError("Opção inválida!");
            }

        } while (option != 8);
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

    }

    // Lista matrículas que possuem saldo pendente
    private void listPendingEnrollments() {
        OperationResult<ArrayList<Enrollment>> result = fitManager.listPendingEnrollments();
        if(!result.isSuccess()){
            ui.showError(result.getMessage());
            return;
        }
        ArrayList<Enrollment> pending = result.getData();
        ui.showMessage("===== MATRÍCULAS PENDENTES =====");

        for(int i = 0; i < pending.size(); i++){
            ui.showMessage(pending.get(i).toString());
        }
        ui.showMessage("Total: " + pending.size() + " matrícula(s) pendente(s).");
    }

    // Lista todas as matrículas cadastradas no sistema
    private void listAllEnrollments() {

        OperationResult<ArrayList<Enrollment>> result = fitManager.listEnrollments();
        if(!result.isSuccess()){
            ui.showMessage(result.getMessage());
            return;
        }

        ArrayList<Enrollment> enrollments = result.getData();
        ui.showMessage("===== TODAS AS MATRÍCULAS =====");
        for(int i = 0; i < enrollments.size(); i++){
            ui.showMessage(enrollments.get(i).toString());
        }
        ui.showMessage("Total: " + enrollments.size());
    }

    // Lista todos os planos disponíveis no sistema
    private void listAllPlans() {
        OperationResult<ArrayList<Plan>> result = fitManager.listPlans();
        if(!result.isSuccess()){
            ui.showMessage(result.getMessage());
            return;
        }
        ArrayList<Plan> plans = result.getData();
        ui.showMessage("===== TODOS OS PLANOS =====");
        for(int i = 0; i < plans.size(); i++){
            Plan p = plans.get(i);
            ui.showMessage(p.toString());
        }
        ui.showMessage("Total: " + plans.size());
    }



    // Busca um aluno pelo CPF informado pelo utilizador
    private void findStudentByCpf() {

        String cpf = ui.getInput("Digite o CPF do aluno:");
        if(cpf.isEmpty()){
            ui.showError("CPF não informado. Consulta cancelada.");
            return;
        }

        OperationResult<Student> result = fitManager.findStudentByCpf(cpf);
        if(!result.isSuccess()){
            ui.showMessage(result.getMessage());
            return;
        }

        Student student = result.getData();

        ui.showMessage(student.toString());
    }

    // Busca um plano pelo nome informado pelo utilizador
    private void findPlanByName() {

        String name = ui.getInput("Digite o nome do plano:");
        if(name.isEmpty()){
            ui.showError("Nome não informado. Consulta cancelada.");
            return;
        }

        OperationResult<Plan> result = fitManager.findPlanByName(name);
        if(!result.isSuccess()){
            ui.showMessage(result.getMessage());
            return;
        }
        Plan plan = result.getData();
        ui.showMessage(plan.toString());
    }

    // Busca matrícula ativa de um aluno pelo CPF
    private void findActiveEnrollmentByCpf() {

        String cpf = ui.getInput("Digite o CPF do aluno:");
        if(cpf.isEmpty()){
            ui.showError("CPF não informado. Consulta cancelada.");
            return;
        }

        OperationResult<Enrollment> result = fitManager.findActiveEnrollmentByStudent(cpf);
        if(!result.isSuccess()){
            ui.showMessage(result.getMessage());
            return;
        }
        Enrollment enrollment = result.getData();
        ui.showMessage(enrollment.toString());
    }


}