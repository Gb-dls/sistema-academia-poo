package ui;

import application.FitManager;

// Classe responsável pelo menu principal do sistema, partir daqui o usuário escolhe qual área acessar
public class MainMenu {


    // ================= ATRIBUTOS =================

    private final UserInterface ui;             // Responsável por toda entrada e saída de dados no console
    private final FitManager fitManager;        // Gerenciador central que faz a ponte entre a UI e os serviços
    private final StudentMenu studentMenu;      // Menu específico para operações com alunos
    private final PlanMenu planMenu;            // Menu específico para operações com planos
    private final EnrollmentMenu enrollmentMenu;    // Menu específico para operações com matrículas
<<<<<<< feature/reports
    private final ReportsMenu reportsMenu;// Menu específico para operações com relatorios
=======
>>>>>>> stage-1




    // ================= CONSTRUTOR =================
    // Recebe todas as dependências prontas (criadas no Main)
<<<<<<< feature/reports
    public MainMenu(UserInterface ui, FitManager fitManager, StudentMenu studentMenu, PlanMenu planMenu, EnrollmentMenu enrollmentMenu, ReportsMenu reportsMenu) {
=======
    public MainMenu(UserInterface ui, FitManager fitManager, StudentMenu studentMenu, PlanMenu planMenu, EnrollmentMenu enrollmentMenu) {
>>>>>>> stage-1
        this.ui = ui;
        this.fitManager = fitManager;
        this.studentMenu = studentMenu;
        this.planMenu = planMenu;
        this.enrollmentMenu = enrollmentMenu;
<<<<<<< feature/reports
        this.reportsMenu = reportsMenu;
=======
>>>>>>> stage-1
    }



    // ================= MENU PRINCIPAL =================

    // Inicia o loop do menu principal
    // O sistema fica rodando nesse loop até o usuário escolher a opção de sair.
    public void start() {

        String option;
        do {                                                     // Exibe as opções disponíveis no menu principal
            ui.showMenu("==== FITMANAGER ====",
                    """
                    1 - Gerenciar alunos
                    2 - Gerenciar planos
                    3 - Gerenciar matrículas
<<<<<<< feature/reports
                    4 - Relatórios/ listagens
                    5 - Sair
=======
                    4 - Sair
>>>>>>> stage-1
                    """
            );

            option = ui.getInput("");   // Le a opção digitada pelo usuário

            switch (option) {       // Direciona para o menu correspondente à opção escolhida

                case "1" -> studentMenu.start();     // Se escolher "1", entra no menu de alunos

                case "2" -> planMenu.start();      // Se escolher "2", entra no menu de planos

                case "3" -> enrollmentMenu.start(); // Se escolher "3", entra no menu de matriculas

<<<<<<< feature/reports
                case "4" -> reportsMenu.start(); // Se escolher "", entra no menu de relatorios

                case "5" -> ui.showMessage("Saindo..."); // Exibe mensagem de saída e encerra o loop
=======
                case "4" -> ui.showMessage("Saindo..."); // Exibe mensagem de saída e encerra o loop
>>>>>>> stage-1

                default -> ui.showError("Opção inválida!"); // Qualquer outra opção é invalida
            }

<<<<<<< feature/reports
        } while (!option.equals("5"));  // Continua executando enquanto a opção não for a de saida
=======
        } while (!option.equals("4"));  // Continua executando enquanto a opção não for a de saida
>>>>>>> stage-1
    }
}