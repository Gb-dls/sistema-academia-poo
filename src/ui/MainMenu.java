package ui;

import application.FitManager;

// Classe responsável pelo menu principal do sistema, partir daqui o usuário escolhe qual área acessar
public class MainMenu {


    // ================= ATRIBUTOS =================

    private final UserInterface ui;             // Responsável por toda entrada e saída de dados no console
    private final FitManager fitManager;        // Gerenciador central que faz a ponte entre a UI e os serviços
    private final StudentMenu studentMenu;      // Menu específico para operações com alunos
    private final PlanMenu planMenu;            // Menu específico para operações com planos




    // ================= CONSTRUTOR =================
    // Recebe todas as dependências prontas (criadas no Main)
    public MainMenu(UserInterface ui, FitManager fitManager, StudentMenu studentMenu, PlanMenu planMenu) {
        this.ui = ui;
        this.fitManager = fitManager;
        this.studentMenu = studentMenu;
        this.planMenu = planMenu;
    }



    // ================= MENU PRINCIPAL =================

    // Inicia o loop do menu principal
    // O sistema fica rodando nesse loop até o usuário escolher a opção de sair
    public void start() {

        String option;
        do {                                                     // Exibe as opções disponíveis no menu principal
            ui.showMenu("==== FITMANAGER ====",
                    """
                    1 - Gerenciar alunos
                    2 - Gerenciar planos
                    3 - Sair
                    """
            );

            option = ui.getInput("");   // Le a opção digitada pelo usuário

            switch (option) {       // Direciona para o menu correspondente à opção escolhida

                case "1" -> studentMenu.start();     // Se escolher "1", entra no menu de alunos

                case "2" -> planMenu.start();      // Se escolher "2", entra no menu de planos

                case "3" -> ui.showMessage("Saindo..."); // Exibe mensagem de saída e encerra o loop

                default -> ui.showError("Opção inválida!"); // Qualquer outra opção é invalida
            }

        } while (!option.equals("3"));  // Continua executando enquanto a opção não for a de saida
    }
}