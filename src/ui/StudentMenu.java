package ui;

import domain.Student;
import application.FitManager;
import application.OperationResult;
import exceptions.ValidationException;
import exceptions.BusinessException;
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
        boolean sucesso = false;

        while (!sucesso) {
            try {
                // A primeira entrada funciona como um botão de "Cancelar" se vier vazia
                String name = ui.getInput("Nome (ou deixe em branco para cancelar):");
                if (name.isEmpty()) {
                    ui.showMessage("Operação cancelada pelo usuário.");
                    return;
                }

                String cpf = ui.getInput("CPF:");
                String contact = ui.getInput("Contato:");
                String email = ui.getInput("E-mail:");
                java.time.LocalDate birthDate = ui.getDate("Data de nascimento");

                // Se o usuário fechar a janela de data na UI Gráfica
                if (birthDate == null) {
                    ui.showMessage("Operação cancelada pelo usuário.");
                    return;
                }

                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String birth = birthDate.format(formatter);

                OperationResult<Student> result = fitManager.registerStudent(name, cpf, contact, email, birth);
                ui.showMessage(result.getMessage());
                sucesso = true;

            } catch (ValidationException | BusinessException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("Erro inesperado no sistema: " + e.getMessage());
                sucesso = true;
            }
        }
    }

    private void updateStudent() {
        boolean sucesso = false;

        while (!sucesso) {
            try {
                String cpf = ui.getInput("CPF do aluno a ser editado (ou vazio para cancelar):");
                if (cpf.isEmpty()) return;

                String name = ui.getInput("Novo nome:");
                String contact = ui.getInput("Novo contato:");
                String email = ui.getInput("Novo e-mail:");
                java.time.LocalDate birthDate = ui.getDate("Nova data de nascimento");

                if (birthDate == null) return;

                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String birth = birthDate.format(formatter);

                OperationResult<Student> result = fitManager.updateStudent(cpf, name, contact, email, birth);
                ui.showMessage(result.getMessage());
                ui.showMessage(result.getData().toString());
                sucesso = true;

            } catch (ValidationException | BusinessException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("Erro inesperado: " + e.getMessage());
                sucesso = true;
            }
        }
    }

    private void deleteStudent() {
        boolean sucesso = false;

        while (!sucesso) {
            try {
                String cpf = ui.getInput("CPF do aluno a inativar (ou vazio para cancelar):");
                if (cpf.isEmpty()) return;

                OperationResult<Void> result = fitManager.removeStudent(cpf);
                ui.showMessage(result.getMessage());
                sucesso = true;

            } catch (BusinessException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("Erro inesperado: " + e.getMessage());
                sucesso = true;
            }
        }
    }


    private void findByCpf() {
        String cpf = ui.getInput("CPF:");
        if (cpf.isEmpty()) {
            ui.showMessage("Consulta cancelada.");
            return;
        }

        OperationResult<Student> result = fitManager.findStudentByCpf(cpf);
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
            ui.showMessage(result.getData().toString());
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
            ui.showMessage(result.getMessage());
        }
    }
}