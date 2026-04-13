package ui;

import domain.Student;

import application.FitManager;
import ui.UserInterface;

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

        } while (!option.equals("6"));      // continua até escolher sair
    }


    // ================= CADASTRAR ALUNO =================
    private void registerStudent() {

        String name = ui.getInput("Nome");
        String cpf = ui.getInput("CPF");
        String email = ui.getInput("E-mail");
        String contact = ui.getInput("Contato");
        String birth = ui.getInput("Data de nascimento (yyyy-mm-dd)");

        // Cria o objeto Student e aqui ocorre conversão da data
        Student student = new Student(name, cpf, contact, email, java.time.LocalDate.parse(birth));

        // Envia para o sistema (FitManager)
        var result = fitManager.registerStudent(student);

        // Mostra resultado
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
            System.out.println(result.getData());
        } else {
            ui.showError(result.getMessage());
        }
    }

    // ================= ATUALIZAR ALUNO =================
    private void updateStudent() {

        String cpf = ui.getInput("CPF do aluno");

        String name = ui.getInput("Novo nome");
        String email = ui.getInput("Novo e-mail");
        String contact = ui.getInput("Novo contato");
        // Entrada da nova dat
        String birthDateInput = ui.getInput("Nova data de nascimento (yyyy-MM-dd)");

        // Conversão da String para LocalDate

        java.time.LocalDate birthDate = java.time.LocalDate.parse(birthDateInput);

        // Chama o serviço para atualizar
        var result = fitManager.updateStudent(cpf, name, contact, email, birthDate);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString()); // mostra aluno atualizado
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
    private void listAll() {
        var result = fitManager.listStudents();

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            System.out.println(result.getData());
        } else {
            ui.showError(result.getMessage());
        }
    }
}