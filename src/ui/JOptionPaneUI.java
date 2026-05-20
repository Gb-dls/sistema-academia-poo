package ui;

import javax.swing.JOptionPane;

// Interface grafica utilizando JOptionPane
public class JOptionPaneUI implements UserInterface {

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
}