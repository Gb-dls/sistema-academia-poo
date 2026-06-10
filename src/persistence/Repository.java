package persistence;

import java.util.ArrayList;

public abstract class Repository<T> {

    protected ArrayList<T> items;

    public Repository() {
        this.items = new ArrayList<>();
    }

    // ================= OPERAÇÕES BASE =================

    public void add(T item) {
        items.add(item);
    }

    public void remove(T item) {
        items.remove(item);
    }

    public ArrayList<T> listAll() {
        return new ArrayList<>(items);
    }

    public int count() {
        return items.size();
    }

    // ================= PERSISTÊNCIA =================

    public abstract void save(String filePath);

    public abstract void load(String filePath);
}