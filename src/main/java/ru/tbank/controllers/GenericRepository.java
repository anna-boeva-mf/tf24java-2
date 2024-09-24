package ru.tbank.controllers;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@Component
public class GenericRepository<T> {
    private final ConcurrentHashMap<String, T> storage = new ConcurrentHashMap<>();
    public T findById(String id) {
        return storage.get(id);
    }

    public void save(String id, T entity) {
        storage.put(id, entity);
    }

    public void delete(String id) {
        storage.remove(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
}
