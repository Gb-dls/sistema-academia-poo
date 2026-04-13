package ui;

import application.FitManager;

// Classe principal do programa (ponto de entrada)
public class Main {

    public static void main(String[] args) {

        // Cria a interface de interação com o usuário (entrada/saída)
        UserInterface ui = new UserInterface();

        //Cria o gerenciador principal do sistema (regra de negócio)
        FitManager fitManager = new FitManager();

        //Cria o menu de alunos e passa a UI e o FitManager para ele poder interagir com o usuário e o sistema
        StudentMenu studentMenu = new StudentMenu(ui, fitManager);

        // Cria o menu principal do sistema e também recebe a UI, o FitManager e o menu de alunos
        MainMenu mainMenu = new MainMenu(ui, fitManager, studentMenu);

        mainMenu.start();       //Inicia o sistema (começando pelo menu principal)
    }
}