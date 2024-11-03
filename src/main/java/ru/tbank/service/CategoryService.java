package ru.tbank.service;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.repository.CategoryRepository;
import ru.tbank.entities.Category;

@Slf4j
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Collection<Category> getAllCategories() {
        log.info("Получение всех категорий");
        return this.categoryRepository.findAll();
    }

    public Category getCategoryById(int id) {
        log.info("Получение категории по ID");
        return this.categoryRepository.findById(id);
    }

    public int createCategory(Category category) {
        log.info("Добавление новой категории");
        int id = this.categoryRepository.genId();
        category.setId(id);
        return this.categoryRepository.save(id, category);
    }

    public void updateCategory(int id, Category category) {
        log.info("Обновление категории");
        category.setId(id);
        this.categoryRepository.save(id, category);
    }

    public void deleteCategory(int id) {
        log.info("Удаление категории");
        this.categoryRepository.delete(id);
    }
}
