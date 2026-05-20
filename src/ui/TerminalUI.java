package ui;

import java.util.Scanner;


//Interface pelo terminal
public class TerminalUI implements UserInterface {
    private final Scanner scanner;  // Scanner usado para ler dados digitados no terminal

    public TerminalUI() {
        this.scanner = new Scanner(System.in);
    }

    // Le a entrada digitada pelo usuario
    @Override
    public String getInput(String message) {
        System.out.print(message + " ");
        return scanner.nextLine();
    }

    // Exibe uma mensagem comum
    @Override
    public void showMessage(String message) {
        System.out.println("\n" + message);
    }

    // Exibe uma mensagem de erro
    @Override
    public void showError(String message) {
        System.out.println("\n" + message);
    }

    // Exibe o menu no terminal
    @Override
    public void showMenu(String title, String options) {
        System.out.println("\n==== " + title.toUpperCase() + " ====");
        System.out.println(options);
        System.out.print("Escolha uma opção:");
    }
}