package ui;

import java.util.Scanner;

// Classe responsável por TODA interação com o usuário (entrada e saída)
public class UserInterface {

    // ================= ATRIBUTOS =================

    // Scanner é a ferramenta do Java para ler o que o usuário digita no console
    private final Scanner scanner;

    // ================= CONSTRUTOR =================

    // Inicializa o Scanner apontando para o console (System.in)
    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    // ================= ENTRADA =================

    // Exibe uma mensagem de instrução e retorna o que o usuário digitou
    public String getInput(String message) {
        System.out.print(message + " "); // exibe o rótulo do campo
        return scanner.nextLine();        // le tudo que o usuario digitou ate pressionar ENTER
    }

    // ================= SAÍDA =================

    // Exibe uma mensagem comum no console
    public void showMessage(String message) {
        System.out.println("\n" + message);
    }

    // Exibe uma mensagem de erro no console de erros (System.err)
    public void showError(String message) {
        System.out.println("\n\u001B[31mERRO: " + message + "\u001B[0m");
    }

    // Exibe o cabeçalho e as opções de um menu
    public void showMenu(String title, String options) {
        System.out.println("\n==== " + title.toUpperCase() + " ====");
        System.out.println(options);
        System.out.print("Escolha uma opção");
    }

}