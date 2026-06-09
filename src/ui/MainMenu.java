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
    private final ReportsMenu reportsMenu;// Menu específico para operações com relatorios


    // ================= CONSTRUTOR =================
    // Recebe todas as dependências prontas (criadas no Main)
    public MainMenu(UserInterface ui, FitManager fitManager, StudentMenu studentMenu, PlanMenu planMenu, EnrollmentMenu enrollmentMenu, ReportsMenu reportsMenu) {
        this.ui = ui;
        this.fitManager = fitManager;
        this.studentMenu = studentMenu;
        this.planMenu = planMenu;
        this.enrollmentMenu = enrollmentMenu;
        this.reportsMenu = reportsMenu;
    }

    // ================= MENU PRINCIPAL =================

    // Inicia o loop do menu principal
    // O sistema fica rodando nesse loop até o usuário escolher a opção de sair.

    public void start() {

        int option;
        do {
            ui.showMenu("FITMANAGER",
                    """
                    1 - Gerenciar alunos
                    2 - Gerenciar planos
                    3 - Gerenciar matrículas
                    4 - Relatórios/ listagens
                    5 - Sair
                    """
            );

            option = ui.getInt("");   // Le a opção digitada pelo usuário

            // TRATAMENTO DO CANCELAR: Se o usuário clicou em Cancelar ou fechou no "X"
            if (option == -1) {
                fitManager.saveAll(); // Tenta salvar os dados antes de sair por segurança
                // Valida se o salvamento ocorreu com sucesso
                if (fitManager.isSucessoUltimoSalvamento()) {
                    break; // Se salvou tudo ok, encerra o loop com segurança
                } else {
                    ui.showError("Gravação falhou! O sistema NÃO foi fechado para evitar perda de dados.");
                    option = 0;
                }
            }
            switch (option) {       // Direciona para o menu correspondente à opção escolhida

                case 1 -> studentMenu.start();     //  entra no menu de alunos

                case 2 -> planMenu.start();      //  entra no menu de planos

                case 3 -> enrollmentMenu.start(); //  entra no menu de matriculas

                case 4 -> reportsMenu.start(); //  entra no menu de relatorios

                case 5 ->{
                    fitManager.saveAll(); // Executa o método void direto do seu DataManager
                    // Faz a validação baseada no estado interno
                    ui.showMessage("Saindo...");
                    if (!fitManager.isSucessoUltimoSalvamento()) {
                        ui.showError("Gravação falhou! O sistema NÃO foi fechado para evitar perda de dados.");
                        option = 0; // Altera para 0 para o do-while NÃO fechar o programa
                    }
                }
                default -> ui.showError("Opção inválida!"); // Qualquer outra opção é invalida
            }

        } while (option != 5);

    }
}