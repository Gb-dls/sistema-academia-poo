package ui;

import application.FitManager;

// Classe responsavel pelo menu principal do sistema
public class MainMenu {

    private final UserInterface ui;           // Interface de interação com o usuário (entrada e saida)
    private final FitManager fitManager;     // Classe que faz a ponte com a lógica do sistema
    private final StudentMenu studentMenu;  // Menu especifico para gerenciar alunos

    // Construtor que recebe todos os .java necessarios
    public MainMenu(UserInterface ui, FitManager fitManager, StudentMenu studentMenu) {
        this.ui = ui;
        this.fitManager = fitManager;
        this.studentMenu = studentMenu;
    }

    // Método que inicia o menu principal
    public void start() {

        String option;

        // Loop para manter o menu rodando até o usuário escolher sair
        do {
            ui.showMenu("==== FITMANAGER ====",
                    """
                    1 - Gerenciar alunos
                    2 - Sair
                    """
            );

            option = ui.getInput("");   // Le a opção digitada pelo usuário

            switch (option) {       // Estrutura de decisão para tratar a opção escolhida

                case "1" -> studentMenu.start();     // Se escolher "1", entra no menu de alunos

                case "2" -> ui.showMessage("Saindo...");

                default -> ui.showError("Opção inválida!"); // Qualquer outra opção é invalida
            }

        } while (!option.equals("2"));  // Continua executando enquanto a opção não for a de saida
    }
}