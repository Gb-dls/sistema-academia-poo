package application;

import java.util.ArrayList;

public abstract class Repository<T> {

    protected ArrayList<T> items;

    public Repository() {
        this.items = new ArrayList<>();
    }

    public ArrayList<T> listAll() {
        return new ArrayList<>(items);
    }

    public int count() {
        return items.size();
    }

    public abstract void save(String filePath);

    public abstract void load(String filePath);
}