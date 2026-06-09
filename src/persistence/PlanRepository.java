package persistence;

import domain.plan.Plan;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import exceptions.*;

public class PlanRepository extends Repository<Plan> {

    public PlanRepository() {
        super();
    }

    // ================= OPERAÇÕES DE COLEÇÃO =================

    public Plan findByName(String name) {
        for (Plan p : items) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public boolean nameExists(String name) {
        return findByName(name) != null;
    }

    public void sortByName() {
        items.sort(Comparator.comparing(Plan::getName));
    }


    // ================= PERSISTÊNCIA  =================

    @Override
    public void save(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(items);
        } catch (IOException e) {
            throw new WriteFailureException(filePath, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            items = (ArrayList<Plan>) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new CorruptedFileException(filePath, e);
        }
    }
}