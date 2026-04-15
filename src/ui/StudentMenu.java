package ui;

import domain.Student;

import application.FitManager;
import ui.UserInterface;
import application.OperationResult;
import java.util.List;

// Classe responsável pelo menu de gerenciamento de alunos
public class StudentMenu {

    // Interface responsável pela comunicação com o usuário (entrada/saída)
    private final UserInterface ui;

    // Classe principal de regras de negócio do sistema
    private final FitManager fitManager;

    // Construtor: recebe as dependências necessárias para o menu funcionar
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

        // Coleta dados do usuário
        String name    = ui.getInput("Nome");
        String cpf     = ui.getInput("CPF");
        String contact = ui.getInput("Contato");
        String email   = ui.getInput("E-mail");
        String birth   = ui.getInput("Data de nascimento (dd/MM/yyyy)");

        // Chama camada de negócio para cadastrar aluno
        var result = fitManager.registerStudent(name, cpf, contact, email, birth);

        // Exibe resultado da operação
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= BUSCAR POR CPF =================
    private void findByCpf() {

        // Solicita CPF ao usuário
        String cpf = ui.getInput("CPF");

        // Busca aluno no sistema
        var result = fitManager.findStudentByCpf(cpf);

        // Exibe resultado
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= ATUALIZAR ALUNO =================
    private void updateStudent() {

        // Coleta novos dados do aluno
        String cpf     = ui.getInput("CPF do aluno");
        String name    = ui.getInput("Novo nome");
        String contact = ui.getInput("Novo contato");
        String email   = ui.getInput("Novo e-mail");
        String birth   = ui.getInput("Nova data de nascimento (dd/MM/yyyy)");

        // Atualiza aluno no sistema
        var result = fitManager.updateStudent(cpf, name, contact, email, birth);

        // Exibe resultado
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= EXCLUIR (INATIVAR) =================
    private void deleteStudent() {

        // Solicita CPF do aluno
        String cpf = ui.getInput("CPF");

        // Inativa aluno no sistema
        var result = fitManager.removeStudent(cpf);

        // Exibe resultado
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= LISTAR TODOS =================
    private void listAll() {

        // Busca todos os alunos cadastrados
        var result = fitManager.listStudents();

        // Converte retorno para lista de alunos
        if (result.isSuccess()) {
            List<Student> students = (List<Student>) result.getData();

            ui.showMessage("===== LISTA DE ALUNOS =====");
            // Percorre e exibe cada aluno
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