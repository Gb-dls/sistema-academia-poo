package ui;

import application.FitManager;

// Ponto de entrada do sistema
public class Main {

    public static void main(String[] args) {

        // Interface de interação com o usuário
        UserInterface ui = new UserInterface();

        // Gerenciador central do sistema
        FitManager fitManager = new FitManager();

        // Instanciação dos menus específicos
        StudentMenu studentMenu = new StudentMenu(ui, fitManager);
        PlanMenu planMenu = new PlanMenu(ui, fitManager);
        EnrollmentMenu enrollmentMenu = new EnrollmentMenu(ui, fitManager);
        ReportsMenu reportsMenu = new ReportsMenu(ui, fitManager);

        // Menu principal recebendo todas as dependências, incluindo o novo menu de relatórios
        MainMenu mainMenu = new MainMenu(ui, fitManager, studentMenu, planMenu, enrollmentMenu, reportsMenu);

        // Inicia o sistema
        mainMenu.start();
    }
}