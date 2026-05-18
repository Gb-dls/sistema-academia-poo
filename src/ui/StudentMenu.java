package ui;

import domain.Student;
import application.FitManager;
import application.OperationResult;
import java.util.List;

public class StudentMenu {

    private final UserInterface ui;
    private final FitManager fitManager;

    public StudentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void start() {
        String option;
        do {
            ui.showMenu("GERENCIAR ALUNOS", """
                1 - Cadastrar novo aluno
                2 - Consultar por CPF
                3 - Editar cadastro
                4 - Excluir aluno
                5 - Listar todos
                6 - Voltar
                """);
            option = ui.getInput("");
            switch (option) {
                case "1" -> registerStudent();
                case "2" -> findByCpf();
                case "3" -> updateStudent();
                case "4" -> deleteStudent();
                case "5" -> listAll();
                case "6" -> ui.showMessage("Voltando ao menu principal...");
                default -> ui.showError("Opção inválida!");
            }
        } while (!option.equals("6"));
    }

    private void registerStudent() {
        String name    = ui.getInput("Nome:");
        String cpf     = ui.getInput("CPF:");
        String contact = ui.getInput("Contato:");
        String email   = ui.getInput("E-mail:");
        String birth   = ui.getInput("Data de nascimento (dd/MM/yyyy):");

        OperationResult result = fitManager.registerStudent(name, cpf, contact, email, birth);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

    private void findByCpf() {
        String cpf = ui.getInput("CPF:");
        OperationResult result = fitManager.findStudentByCpf(cpf);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void updateStudent() {
        String cpf     = ui.getInput("CPF do aluno:");
        String name    = ui.getInput("Novo nome:");
        String contact = ui.getInput("Novo contato:");
        String email   = ui.getInput("Novo e-mail:");
        String birth   = ui.getInput("Nova data de nascimento (dd/MM/yyyy):");

        OperationResult result = fitManager.updateStudent(cpf, name, contact, email, birth);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void deleteStudent() {
        String cpf = ui.getInput("CPF:");
        OperationResult result = fitManager.removeStudent(cpf);
        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

    private void listAll() {
        OperationResult result = fitManager.listStudents();
        if (result.isSuccess()) {
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