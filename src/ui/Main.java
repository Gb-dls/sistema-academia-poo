package ui;

import application.FitManager;

// Ponto de entrada do sistema, é aqui que o programa começa a executar
public class Main {

    public static void main(String[] args) {

        // Cria a interface de interação com o usuário, responsável por toda entrada  e saída  de dados no console
        UserInterface ui = new UserInterface();


        // Cria o gerenciador central do sistema, ele que recebe os dados da UI, limpa, converte e repassa para os serviços
        FitManager fitManager = new FitManager();


        // Cria o menu de alunos
        // Recebe a UI: para exibir e coletar dados e o FitManager: para executar as operações
        StudentMenu studentMenu = new StudentMenu(ui, fitManager);

        // Cria o menu de planos
        PlanMenu planMenu = new PlanMenu(ui, fitManager);


        // Cria o menu principal do sistema e recebe todos os outros menus para poder navegar entre eles
        MainMenu mainMenu = new MainMenu(ui, fitManager, studentMenu, planMenu);

        // Inicia o sistema pelo menu principal e fica rodando até o usuário escolher sair
        mainMenu.start();
    }
}