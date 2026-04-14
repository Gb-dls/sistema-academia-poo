package ui;

import domain.Enrollment;
import domain.PaymentType;
import domain.Plan;
import domain.Student;
import application.FitManager;
import application.OperationResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class EnrollmentMenu {

    private final UserInterface ui;
    private final FitManager fitManager;

    public EnrollmentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    // Método principal do menu de matrículas
    public void start() {

        String option;

        do {
            ui.showMenu(
                    "GERENCIAR MATRÍCULAS",
                    """
                            1 - Realizar matrícula
                            2 - Registrar pagamento
                            3 - Cancelar matrícula
                            4 - Consultar matrícula ativa
                            5 - Listar histórico
                            6 - Voltar
                            """
            );

            option = ui.getInput("");

            switch (option) {
                case "1" -> enroll();               // Realizar matrícula

                case "2" -> registerPayment();       // Registrar pagamento

                case "3" -> cancel();                // Cancelar matrícula

                case "4" -> findActiveByStudent();   // Consultar matrícula ativa por CPF

                case "5" -> listAll();               // Listar histórico de matrículas

                case "6" -> ui.showMessage("Voltando ao menu principal...");

                default -> ui.showError("Opção inválida!");
            }

        } while (!option.equals("6"));
    }
}

