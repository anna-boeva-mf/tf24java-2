package ru.tbank.repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tbank.exception.EntityNotFoundException;

@Slf4j
@Component
public class GenericRepository<ID, T> {
    private final ConcurrentHashMap<ID, T> storage = new ConcurrentHashMap<>();

    public T findById(ID id) {
        T entity = this.storage.get(id);
        if (entity == null) {
            log.info("Сущность с ID={} не найдена", id);
            throw new EntityNotFoundException("Сущность с ID=" + id + " не найдена");
        }
        return entity;
    }

    public ID save(ID id, T entity) {
        this.storage.put(id, entity);
        return id;
    }

    public void delete(ID id) {
        T entity = this.storage.remove(id);
        if (entity == null) {
            log.warn("Сущность с ID={} не найдена", id);
        }
    }

    public Collection<T> findAll() {
        return this.storage.values();
    }
}
