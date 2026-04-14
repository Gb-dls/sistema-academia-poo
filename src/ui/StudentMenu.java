package ui;

import domain.Student;

import application.FitManager;
import ui.UserInterface;
import application.OperationResult;
import java.util.List;

// Classe responsável pelo menu de gerenciamento de alunos
public class StudentMenu {

    private final UserInterface ui;      // Interface para interação com o usuário
    private final FitManager fitManager;    // Classe que contém a lógica do sistema

    // Construtor que  recebe os .java necessarios
    public StudentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    // Método principal do menu de alunos
    public void start() {

        String option;

        // Loop que mantém o menu ativo até o usuário escolher sair
        do {
            ui.showMenu(
                "GERENCIAR ALUNOS",
                """
                1 - Cadastrar novo aluno
                2 - Consultar por CPF
                3 - Editar cadastro
                4 - Excluir aluno
                5 - Listar todos
                6 - Voltar
                """
            );

            option = ui.getInput("");       // Le a opção do usuário

            switch (option) {       // Decide qual ação executar

                case "1" -> registerStudent();      // cadastra aluno

                case "2" -> findByCpf();            // busca por CPF

                case "3" ->  updateStudent();       // atualiza dados

                case "4" -> deleteStudent();         // exclui (inativa)

                case "5" -> listAll();          // lista todos

                case "6" -> ui.showMessage("Voltando ao menu principal...");

                default -> ui.showError("Opção inválida!");
            }

        } while (!option.equals("6"));
    }


    // ================= CADASTRAR ALUNO =================
    private void registerStudent() {
        String name    = ui.getInput("Nome");
        String cpf     = ui.getInput("CPF");
        String contact = ui.getInput("Contato");
        String email   = ui.getInput("E-mail");
        String birth   = ui.getInput("Data de nascimento (yyyy-MM-dd)");

        var result = fitManager.registerStudent(name, cpf, contact, email, birth);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= BUSCAR POR CPF =================
    private void findByCpf() {
        String cpf = ui.getInput("CPF");
        var result = fitManager.findStudentByCpf(cpf);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= ATUALIZAR ALUNO =================
    private void updateStudent() {
        String cpf     = ui.getInput("CPF do aluno");
        String name    = ui.getInput("Novo nome");
        String contact = ui.getInput("Novo contato");
        String email   = ui.getInput("Novo e-mail");
        String birth   = ui.getInput("Nova data de nascimento (yyyy-MM-dd)");

        var result = fitManager.updateStudent(cpf, name, contact, email, birth);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= EXCLUIR (INATIVAR) =================
    private void deleteStudent() {
        String cpf = ui.getInput("CPF");
        var result = fitManager.deleteStudent(cpf);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= LISTAR TODOS =================
    /*private void listAll() {
        var result = fitManager.listStudents();

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }*/


    private void listAll() {
        var result = fitManager.listStudents();

        if (result.isSuccess()) {
            // Converte o objeto para lista de Students
            List<Student> students = (List<Student>) result.getData();

            ui.showMessage("===== LISTA DE ALUNOS =====");
            for (int i = 0; i < students.size(); i++) {
                ui.showMessage("---------- " + (i + 1) + " ----------");
                ui.showMessage(students.get(i).toString());
            }
            ui.showMessage("===========================");
        } else {
            ui.showError(result.getMessage());
        }
    }
}