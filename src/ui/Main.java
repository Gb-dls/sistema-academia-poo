package ui;

import application.FitManager;
import javax.swing.JOptionPane;

// Ponto de entrada do sistema
public class Main {

    public static void main(String[] args) {

        // Usa as janelas graficas para poder escolher a 'interface'
        String[] opcoes = {"Terminal", "Gráfica"};      // Opções de 'interface'
        int escolha = -1;

        // Exibe janela para seleção da 'interface'
        while (escolha != 0 && escolha != 1) {
            int resultado = JOptionPane.showOptionDialog(null,"Seja Bem-Vindo!\nEscolha a interface do sistema:", "FitManager", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

            // Encerra o programa se o usuario fechar a janela
            if (resultado == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            }
            escolha = resultado;
        }


        // Cria a 'interface' escolhida pelo usuario
        UserInterface ui;
        if (escolha == 0) {
            ui = new TerminalUI();
        } else {
            ui = new JOptionPaneUI();
        }

        // Instancia o gerenciador principal do sistema
        FitManager fitManager = new FitManager();

        // Cria os menus do sistema
        StudentMenu studentMenu       = new StudentMenu(ui, fitManager);
        PlanMenu planMenu             = new PlanMenu(ui, fitManager);
        EnrollmentMenu enrollmentMenu = new EnrollmentMenu(ui, fitManager);
        ReportsMenu reportsMenu       = new ReportsMenu(ui, fitManager);

        //Cria o menu principal
        MainMenu mainMenu = new MainMenu(ui, fitManager, studentMenu, planMenu, enrollmentMenu, reportsMenu);
        mainMenu.start();  //Inicia o sistema

    }
}

