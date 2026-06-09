package persistence;

import domain.Enrollment;
import java.io.Serializable;
import java.util.ArrayList;

// Esta classe serve estritamente como uma "sacola" para agrupar os dados e salvar no arquivo
public class SaveState implements Serializable {
    private static final long serialVersionUID = 1L;

    public ArrayList<Enrollment> items;
    public int nextCode;
}