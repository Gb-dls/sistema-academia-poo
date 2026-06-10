package ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;


//Interface pelo terminal
public class TerminalUI implements UserInterface {
    private final Scanner scanner;  // Scanner usado para ler dados digitados no terminal
    // formatador estático para garantir que a data siga o padrão dd/mm/aaaa
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    /** Realiza a tentativa de conversão do número inserido pelo usuário para inteiro ou double(próximo metódo)
     * e se acaso for inválido ele solicita um novo número até receber um válido */
    @Override
    public int getInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                showError("Entrada inválida! Por favor, digite um número inteiro válido.");
            }
        }
    }


    @Override
    public double getDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                String input = scanner.nextLine().trim();
                // Trata a digitação substituindo vírgula por ponto para o padrão do Java
                String formattedInput = input.replace(",", ".");
                return Double.parseDouble(formattedInput);
            } catch (NumberFormatException e) {
                showError("Entrada inválida! Por favor, digite um valor decimal válido (Ex: 79.90).");
            }
        }
    }

    @Override
    public LocalDate getDate(String message) {
        while (true) {
            try {
                System.out.print(message + " (dd/mm/aaaa): ");
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input, dateFormatter);
            } catch (DateTimeParseException e) {
                showError("Data inválida! Certifique-se de usar o formato dd/mm/aaaa.");
            }
        }
    }

}