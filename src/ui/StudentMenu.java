package ui;

import domain.Student;
import application.FitManager;
import application.OperationResult;
import java.util.ArrayList;

public class StudentMenu {

    private final UserInterface ui;
    private final FitManager fitManager;

    public StudentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void start() {
        int option;
        do {
            ui.showMenu("GERENCIAR ALUNOS", """
                    1 - Cadastrar novo aluno
                    2 - Consultar por CPF
                    3 - Editar cadastro
                    4 - Excluir aluno
                    5 - Listar todos
                    6 - Voltar
                    """);
            option = ui.getInt("");
            switch (option) {
                case 1 -> registerStudent();
                case 2 -> findByCpf();
                case 3 -> updateStudent();
                case 4 -> deleteStudent();
                case 5 -> listAll();
                case 6 -> ui.showMessage("Voltando ao menu principal...");
                default -> ui.showError("Opção inválida!");
            }
        } while (option != 6);
    }

    private void registerStudent() {
        String name = ui.getInput("Nome:");
        if (name.isEmpty()) {
            ui.showError("Nome não informado. Cadastro cancelado.");
            return;
        }
        String cpf = ui.getInput("CPF:");
        if (cpf.isEmpty()) {
            ui.showError("CPF não informado. Cadastro cancelado.");
            return;
        }

        String contact = ui.getInput("Contato:");
        if (contact.isEmpty()) {
            ui.showError("Contato não informado. Cadastro cancelado.");
            return;
        }
        String email = ui.getInput("E-mail:");
        if (email.isEmpty()) {
            ui.showError("E-mail não informado. Cadastro cancelado.");
            return;
        }

        java.time.LocalDate birthDate = ui.getDate("Data de nascimento");
        if (birthDate == null) {
            ui.showError("Operação cancelada pelo usuário. O aluno não foi cadastrado.");
            return;
        }

        // Converte o LocalDate estável para String para mandar ao FitManager sem quebrar nada
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String birth = birthDate.format(formatter);

        OperationResult<Student> result = fitManager.registerStudent(name, cpf, contact, email, birth);

        if (result.isSuccess()) ui.showMessage(result.getMessage());
        else ui.showError(result.getMessage());
    }

    private void findByCpf() {
        String cpf = ui.getInput("CPF:");
        if (cpf.isEmpty()) {
            ui.showError("CPF não informado. Consulta cancelada.");
            return;
        }
        OperationResult<Student> result = fitManager.findStudentByCpf(cpf);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            Student student = result.getData();
            ui.showMessage(student.toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void updateStudent() {
        String cpf = ui.getInput("CPF do aluno:");
        if (cpf.isEmpty()) {
            ui.showError("CPF não informado. Edição cancelada.");
            return;
        }

        String name = ui.getInput("Novo nome:");
        if (name.isEmpty()) {
            ui.showError("Nome não informado. Edição cancelada.");
            return;
        }

        String contact = ui.getInput("Novo contato:");
        if (contact.isEmpty()) {
            ui.showError("Contato não informado. Edição cancelada.");
            return;
        }

        String email = ui.getInput("Novo e-mail:");
        if (email.isEmpty()) {
            ui.showError("E-mail não informado. Edição cancelada.");
            return;
        }

        java.time.LocalDate birthDate = ui.getDate("Nova data de nascimento");

        if (birthDate == null) {
            ui.showError("Operação cancelada pelo usuário. Edição cancelada.");
            return;
        }

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String birth = birthDate.format(formatter);

        OperationResult<Student> result = fitManager.updateStudent(cpf, name, contact, email, birth);

        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            Student student = result.getData();
            ui.showMessage(student.toString());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void deleteStudent() {
        String cpf = ui.getInput("CPF:");
        if (cpf.isEmpty()) {
            ui.showError("CPF não informado. Inativação cancelada.");
            return;
        }
        OperationResult<Void> result = fitManager.removeStudent(cpf);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void listAll() {
        OperationResult<ArrayList<Student>> result = fitManager.listStudents();

        if (result.isSuccess()) {
            ArrayList<Student> students = result.getData();
            ui.showMessage("===== LISTA DE ALUNOS =====");
            for (int i = 0; i < students.size(); i++) {
                ui.showMessage("---------- " + (i + 1) + " ----------");
                ui.showMessage(students.get(i).toString());
            }
        } else {
            ui.showError(result.getMessage());
        }
    }
}
