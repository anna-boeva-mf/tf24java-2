package ru.tbank.services;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tbank.GenericRepository;
import ru.tbank.entities.Category;

@Service
public class CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final GenericRepository<Category> categoryRepository;

    public Collection<Category> getAllCategories() {
        log.info("Получение всех категорий");
        return this.categoryRepository.findAll();
    }

    public Category getCategoryById(int id) {
        return (Category)this.categoryRepository.findById(id);
    }

    public int createCategory(Category category) {
        int id = this.categoryRepository.genId();
        category.setId(id);
        return this.categoryRepository.save(id, category);
    }

    public void updateCategory(int id, Category category) {
        category.setId(id);
        this.categoryRepository.save(id, category);
    }

    public void deleteCategory(int id) {
        this.categoryRepository.delete(id);
    }

    public CategoryService(GenericRepository<Category> categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
}
