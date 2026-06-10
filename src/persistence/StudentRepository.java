package persistence;

import domain.Student;
import java.util.Comparator;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import exceptions.*;

public class StudentRepository extends Repository<Student> {

    public StudentRepository() {
        super();
    }

    // ================= REGRAS ESPECÍFICAS =================

    public Student findByCpf(String cpf) {
        for (Student s : items) {
            if (s.getCpf().equals(cpf)) {
                return s;
            }
        }
        return null;
    }

    public boolean cpfExists(String cpf) {
        return findByCpf(cpf) != null;
    }

    public void sortByName() {
        items.sort(Comparator.comparing(
                Student::getName,
                String.CASE_INSENSITIVE_ORDER
        ));
    }

    // ================= PERSISTÊNCIA =================

    @Override
    public void save(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(items);
        }catch(IOException e){
            throw new WriteFailureException(filePath, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load(String filePath) {
        File file = new File(filePath);
        if(!file.exists()){
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            items = (ArrayList<Student>) ois.readObject();
        }catch(ClassNotFoundException | IOException e){
            throw new CorruptedFileException(filePath, e);
        }
    }
}