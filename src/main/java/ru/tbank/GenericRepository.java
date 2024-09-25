package ru.tbank;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@Component
public class GenericRepository<T> {
    private final ConcurrentHashMap<Integer, T> storage = new ConcurrentHashMap<>();

    public T findById(int id) {
        return storage.get(id);
    }

    public void save(int id, T entity) {
        storage.put(id, entity);
    }

    public void delete(int id) {
        storage.remove(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
}
