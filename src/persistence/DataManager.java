package persistence;

import exceptions.CorruptedFileException;
import exceptions.WriteFailureException;
import ui.UserInterface;
import java.io.File;

public class DataManager {

    private static final String STUDENTS_FILE    = "data/students.ser";
    private static final String PLANS_FILE       = "data/plans.ser";
    private static final String ENROLLMENTS_FILE = "data/enrollments.ser";

    private final StudentRepository    studentRepo;
    private final PlanRepository       planRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final UserInterface ui;

    private boolean sucessoUltimoSalvamento = true;

    // Construtor
    public DataManager(StudentRepository s, PlanRepository p, EnrollmentRepository e, UserInterface ui) {
        this.studentRepo    = s;
        this.planRepo       = p;
        this.enrollmentRepo = e;
        this.ui             = ui;
    }

    public boolean isSucessoUltimoSalvamento() {
        return this.sucessoUltimoSalvamento;
    }

    public void loadAll() {
        safeLoad(studentRepo,    STUDENTS_FILE,    "Alunos");
        safeLoad(planRepo,       PLANS_FILE,       "Planos");
        safeLoad(enrollmentRepo, ENROLLMENTS_FILE, "Matrículas");
    }

    public void saveAll() {
        new File("data").mkdirs();

        boolean allOk = true;
        allOk &= safeSave(enrollmentRepo, ENROLLMENTS_FILE, "Matrículas");
        allOk &= safeSave(planRepo,       PLANS_FILE,       "Planos");
        allOk &= safeSave(studentRepo,    STUDENTS_FILE,    "Alunos");

        this.sucessoUltimoSalvamento = allOk;

        if (allOk) {
            ui.showMessage("Dados salvos com sucesso.");
        } else {
            ui.showError("ATENÇÃO - Um ou mais arquivos não puderam ser salvos.\nOs dados não salvos serão perdidos ao encerrar.");
        }
    }

    private void safeLoad(Repository<?> repo, String filePath, String nomeRepositorio) {
        try {
            repo.load(filePath);
        } catch (CorruptedFileException e) {

            ui.showError("AVISO - Arquivo de " + nomeRepositorio + " corrompido.\n" + "O repositório correspondente será iniciado vazio.\nErro: " + e.getMessage());
        }
    }


    private boolean safeSave(Repository<?> repo, String filePath, String nomeRepositorio) {
        try {
            repo.save(filePath);
            return true;
        } catch (WriteFailureException e) {
            ui.showError("ERRO - Falha ao salvar " + nomeRepositorio + ".\nMotivo: " + e.getMessage());
            return false;
        }
    }
}