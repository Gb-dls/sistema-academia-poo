package ui;

import java.util.Scanner;

// Classe responsável por TODA interação com o usuario (entrada e saida)
public class UserInterface {

    // Scanner para ler dados digitados no console
    private final Scanner scanner;

    // Construtor que inicializa o scanner
    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

   // ===== Entrada de Dados =====
   // Exibe uma mensagem e captura o que o usuário digitar
    public String getInput(String message) {
        System.out.print(message + ": ");   // mostra a mensagem
        return scanner.nextLine();          // le a entrada do usuario
    }


    // ===== Mensagem padrão (sucesso) =====
    // Exibe mensagens comuns (ex: cadastro realizado)
    public void showMessage(String message) {
        System.out.println("\nSucesso\n" + message); //Mensagem de operationResult
    }

    // ===== Mensagem de erro =====
    // Exibe mensagens de erro (usa o System.err para destacar)
    public void showError(String message) {
        System.err.println("\nERRO\n " + message); //Mensagem de operationResult
    }

  // ===== Exibir menu =====
    public void showMenu(String title, String options) {
        System.out.println("\n==== " + title.toUpperCase() + " ====");
        System.out.println(options);
        System.out.print("Escolha uma opção");
    }

    // ========================= PAUSA =========================
    // Faz o sistema esperar o usuário pressionar ENTER
    public void waitEnter() {
        System.out.println("\nPressione ENTER para continuar...");
        scanner.nextLine();     // espera o ENTER
    }
}