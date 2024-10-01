package ru.tbank;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class GenericRepository<T> {
    private final ConcurrentHashMap<Integer, T> storage = new ConcurrentHashMap();
    private static int counter = 1;

    public GenericRepository() {
    }

    public int genId() {
        return counter++;
    }

    public T findById(Integer id) {
        return this.storage.get(id);
    }

    public int save(Integer id, T entity) {
        this.storage.put(id, entity);
        return id;
    }

    public void delete(Integer id) {
        this.storage.remove(id);
    }

    public Collection<T> findAll() {
        return this.storage.values();
    }
}