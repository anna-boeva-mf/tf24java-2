package ru.tbank.repository;

import org.springframework.stereotype.Component;
import ru.tbank.entities.Category;

@Component
public class CategoryRepository extends GenericRepository<Integer, Category> {
    private int counter = 1;

    public int genId() {
        return counter++;
    }

}
