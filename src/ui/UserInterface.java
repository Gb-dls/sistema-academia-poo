package ui;
import java.time.LocalDate;

//padroniza a comunicação para conseguir usar tanto o terminal quanto os popup
public interface UserInterface {

    // Le uma entrada do usuario
    String getInput(String message);

    // Exibe uma mensagem comum
    void showMessage(String message);

    // Exibe uma mensagem de erro
    void showError(String message);

    // Exibe um menu de opções
    void showMenu(String title, String options);

    /** Captura uma entrada do usuário e garante o retorno de um número inteiro válido.
     * Caso o usuário digite algo inválido, a própria implementação trata e repete a pergunta.*/
    int getInt(String message);

    /** Captura uma entrada do usuário e garante o retorno de um número decimal (double) válido.
     * Trata variações regionais como o uso de vírgulas no terminal.*/
    double getDouble(String message);

    /** Captura uma entrada do usuário e garante o retorno de uma data (LocalDate) válida
     * baseada estritamente no padrão de formato nacional (dd/mm/aaaa).*/
    LocalDate getDate(String message);
}


