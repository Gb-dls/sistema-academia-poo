package application;

import domain.Student;
import application.StudentService;
import application.OperationResult;


import java.util.List;
import java.time.LocalDate;


// Classe principal que centraliza as operações e conversa com os serviços
public class FitManager {

    // Serviço responsável por gerenciar alunos
    private final StudentService studentService;

    // Construtor que inicializa o serviço de alunos
    public FitManager() {
        this.studentService = new StudentService();
    }

    // =====ALUNOS=====

    // Cadastra um novo aluno recebe um objeto Student já criado e repassa para o service
    public OperationResult registerStudent(Student student) {
        return studentService.registerStudent(student);
    }

    // Busca um aluno pelo CPF retorna um OperationResult (pode conter o aluno ou erro)
    public OperationResult findStudentByCpf(String cpf) {
        return studentService.findByCpf(cpf);
    }

    // Atualiza os dados de um aluno existente recebe os novos dados e repassa para o service
    public OperationResult updateStudent(String cpf, String name, String contact, String email, LocalDate birthDate) {
        return studentService.updateStudent(cpf, name, contact, email, birthDate);
    }

    // "Deleta" um aluno (na verdade, inativa)
    public OperationResult deleteStudent(String cpf) {
         return studentService.deactivateStudent(cpf);
    }

    // Lista todos os alunos cadastrados
    public OperationResult listStudents() {
        return studentService.listStudents();
    }

    // =========================
    // (FUTURO: PLANOS)
    // =========================

    // public OperationResult createPlan(...) { ... }

    // =========================
    // (FUTURO: MATRÍCULAS)
    // =========================

    // public OperationResult enrollStudent(...) { ... }
}