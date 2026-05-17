package ui;

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
}


