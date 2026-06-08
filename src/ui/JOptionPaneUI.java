package ui;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Interface grafica utilizando JOptionPane
public class JOptionPaneUI implements UserInterface {
    // Formatador para compatibilidade visual idêntica com o formato dd/mm/aaaa
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    // Exibe uma caixa de entrada para o usuario conseguir digitar
    @Override
    public String getInput(String message) {
        String resultado = JOptionPane.showInputDialog(null, message);

        //Caso o usuario cancele ou feche no input retorna a string vazia para voltar ao menu anterior
        if (resultado == null) {
            return "";
        }

        return resultado.trim();   //remove espaços das bordas caso o usuario digite
    }


    // Exibe uma mensagem comum
    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    // Exibe mensagens de erro
    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // Exibe menus em uma janela grafica
    @Override
    public void showMenu(String title, String options) {

        int resultado = JOptionPane.showConfirmDialog(null, options, title, JOptionPane.DEFAULT_OPTION);

        // Encerra o sistema caso o usuario feche a janela nos menus
        if (resultado == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }
    }

    @Override
    public int getInt(String message) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(null, message, "Entrada de Dados", JOptionPane.QUESTION_MESSAGE);

                // PROTEÇÃO CRÍTICA: Trata o clique no botão "Cancelar" ou fecho da janela (X)
                if (input == null) {
                    return -1; // Retorna um sinalizador para o menu saber que o utilizador cancelou
                }

                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                showError("Por favor, insira um número inteiro válido.");
            }
        }
    }

    @Override
    public double getDouble(String message) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(null, message, "Entrada de Dados", JOptionPane.QUESTION_MESSAGE);

                if (input == null) {
                    return -1.0; // Sinalizador de cancelamento para valores decimais
                }

                // Trata a digitação substituindo vírgula por ponto (ex: "49,90" -> "49.90")
                String formattedInput = input.trim().replace(",", ".");
                return Double.parseDouble(formattedInput);
            } catch (NumberFormatException e) {
                showError("Por favor, insira um valor numérico decimal válido.");
            }
        }
    }

    @Override
    public LocalDate getDate(String message) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(null, message + " (dd/mm/aaaa)", "Entrada de Data", JOptionPane.QUESTION_MESSAGE);

                if (input == null) {
                    return null; // Retorna nulo indicando que a operação com data foi cancelada
                }

                return LocalDate.parse(input.trim(), dateFormatter);
            } catch (DateTimeParseException e) {
                showError("Data inválida! Utilize rigorosamente o formato: dd/mm/aaaa.");
            }
        }
    }

}