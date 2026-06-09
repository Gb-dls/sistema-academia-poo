package persistence;

import domain.Enrollment;
import domain.EnrollmentStatus;
import formatters.DateFormatter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import exceptions.*;

public class EnrollmentRepository extends Repository<Enrollment> {

    // nextCode fica aqui pois pertence ao ciclo de vida das matrículas e precisa ser salvo/restaurado junto com elas
    private int nextCode;

    public EnrollmentRepository() {
        super();
        this.nextCode = 1;
    }

    // ================= OPERAÇÕES DE COLEÇÃO =================

    public int useNextCode() {
        return nextCode++;
    }

    public int getNextCode() {
        return nextCode;
    }

    public void setNextCode(int nextCode) {
        this.nextCode = nextCode;
    }

    public Enrollment findByCode(int code) {
        for (Enrollment e : items) {
            if (e.getCode() == code) return e;
        }
        return null;
    }

    public Enrollment findActiveByStudent(String cpf) {
        String cleanCpf = DateFormatter.cleanNumber(cpf);
        for (Enrollment e : items) {
            if (e.getStudent().getCpf().equals(cleanCpf) && e.getStatus() == EnrollmentStatus.ACTIVE) {
                return e;
            }
        }
        return null;
    }

    public List<Enrollment> findAllByStudent(String cpf) {
        String cleanCpf = cpf.replaceAll("\\D", "");
        List<Enrollment> result = new ArrayList<>();

        for (Enrollment e : items) {
            if (e.getStudent().getCpf().replaceAll("\\D", "").equals(cleanCpf)) {
                result.add(e);
            }
        }
        return result;
    }


    // ================= PERSISTÊNCIA =================



    @Override
    public void save(String filePath) {
        // Criamos o objeto que vai empacotar os dados
        SaveState state = new SaveState();
        state.items = this.items;
        state.nextCode = this.nextCode;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(state); // Salva o pacote completo no arquivo
        } catch(IOException e) {
            throw new WriteFailureException(filePath, e);
        }
    }

    @Override
    public void load(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))){
            // Lemos o pacote completo do arquivo
            SaveState state = (SaveState) ois.readObject();
            this.items = state.items;
            this.nextCode = state.nextCode;
        } catch(ClassNotFoundException | IOException e){
            throw new CorruptedFileException(filePath, e);
        }
    }
}