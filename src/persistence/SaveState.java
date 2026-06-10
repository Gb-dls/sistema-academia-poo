package persistence;

import domain.Enrollment;
import java.io.Serializable;
import java.util.ArrayList;

// Esta classe serve para agrupar os dados e salvar no arquivo
public class SaveState implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Enrollment> items;
    private int nextCode;

    public SaveState(ArrayList<Enrollment> items, int nextCode) {
        this.items = items;
        this.nextCode = nextCode;
    }

    public ArrayList<Enrollment> getItems() {
        return items;
    }

    public int getNextCode() {
        return nextCode;
    }
}