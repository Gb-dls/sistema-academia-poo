package application;

import java.util.ArrayList;

// Classe genérica abstrata que serve de base para todos os serviços que guardam dados
public abstract class Repository<T> {

    // A lista genérica protegida (protected) para que os serviços filhos possam acessá-la
    protected ArrayList<T> items;

    public Repository() {
        this.items = new ArrayList<>();
    }

    // Retorna todos os itens
    public ArrayList<T> listAll() {
        return this.items;
    }

    // Retorna a quantidade de itens cadastrados
    public int count() {
        return this.items.size();
    }

    // Métodos abstratos de persistência que os serviços filhos são obrigados a implementar
    public abstract void save(String filePath);

    public abstract void load(String filePath);
}