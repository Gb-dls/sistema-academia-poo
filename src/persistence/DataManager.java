package persistence;

import exceptions.CorruptedFileException;
import exceptions.WriteFailureException;
import ui.UserInterface;

import java.io.File;

public class DataManager {

    private static final String STUDENTS_FILE    = "data/students.ser";
    private static final String PLANS_FILE       = "data/plans.ser";
    private static final String ENROLLMENTS_FILE = "data/enrollments.ser";

    private static final String BACKUP_STUDENTS_FILE    = "backup/students_backup.ser";
    private static final String BACKUP_PLANS_FILE       = "backup/plans_backup.ser";
    private static final String BACKUP_ENROLLMENTS_FILE = "backup/enrollments_backup.ser";

    private final StudentRepository studentRepo;
    private final PlanRepository planRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final UserInterface ui;

    private boolean sucessoUltimoSalvamento = true;

    public DataManager(StudentRepository s,
                       PlanRepository p,
                       EnrollmentRepository e,
                       UserInterface ui) {

        this.studentRepo = s;
        this.planRepo = p;
        this.enrollmentRepo = e;
        this.ui = ui;
    }

    public boolean isSucessoUltimoSalvamento() {
        return this.sucessoUltimoSalvamento;
    }

    public void loadAll() {
        safeLoad(studentRepo, STUDENTS_FILE, "Alunos");
        safeLoad(planRepo, PLANS_FILE, "Planos");
        safeLoad(enrollmentRepo, ENROLLMENTS_FILE, "Matrículas");
    }

    public void saveAll() {

        new File("data").mkdirs();

        boolean allOk = true;

        allOk &= safeSave(enrollmentRepo, ENROLLMENTS_FILE, "Matrículas");
        allOk &= safeSave(planRepo, PLANS_FILE, "Planos");
        allOk &= safeSave(studentRepo, STUDENTS_FILE, "Alunos");

        if (allOk) {

            sucessoUltimoSalvamento = true;
            ui.showMessage("Dados salvos com sucesso.");

        } else {

            ui.showError("ATENÇÃO - Um ou mais arquivos não puderam ser salvos.\n" + "Tentando criar backup de emergência...");
            boolean backupOk = saveEmergencyBackup();
            if (backupOk) {
                sucessoUltimoSalvamento = true;
                ui.showMessage("Backup de emergência criado com sucesso.\n" + "Os dados da sessão foram preservados na pasta 'backup'.");

            } else {
                sucessoUltimoSalvamento = false;
                ui.showError(
                        "Falha no salvamento principal e no backup de emergência.\n" + "O sistema permanecerá aberto para evitar perda de dados."
                );
            }
        }
    }

    private void safeLoad(Repository<?> repo, String filePath, String nomeRepositorio) {

        try {
            repo.load(filePath);

        }catch(CorruptedFileException e) {

            ui.showError("AVISO - Arquivo de " + nomeRepositorio + " corrompido.\n" + "O repositório correspondente será iniciado vazio.\n" + "Erro: " + e.getMessage());
        }
    }

    private boolean safeSave(Repository<?> repo, String filePath, String nomeRepositorio) {

        try {

            repo.save(filePath);
            return true;

        } catch (WriteFailureException e) {

            ui.showError(
                    "ERRO - Falha ao salvar " + nomeRepositorio + ".\n" +
                            "Motivo: " + e.getMessage()
            );

            return false;
        }
    }

    private boolean saveEmergencyBackup() {

        try {
            new File("backup").mkdirs();
            studentRepo.save(BACKUP_STUDENTS_FILE);
            planRepo.save(BACKUP_PLANS_FILE);
            enrollmentRepo.save(BACKUP_ENROLLMENTS_FILE);
            return true;

        } catch (Exception e) {
            ui.showError("Falha ao criar backup de emergência.\n" + "Motivo: " + e.getMessage());
            return false;
        }
    }
}