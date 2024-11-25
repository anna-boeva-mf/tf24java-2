package ru.tbank.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.tbank.db_repository.CategoryRepository;
import ru.tbank.entities.Category;
import ru.tbank.exception.BadRequestException;
import ru.tbank.patterns.CategorySnapshot;
import ru.tbank.patterns.Observer;
import ru.tbank.patterns.Subject;
import ru.tbank.patterns.HistoryManager;

@Slf4j
@Service
public class CategoryService implements Subject {

    @Value("${spring.datasource.username}")
    private String currentUser;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HistoryManager historyManager;

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String action, Object entity) {
        for (Observer observer : observers) {
            observer.update(action, entity);
        }
    }

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        log.info("Получение всех категорий");
        List<Category> categories = this.categoryRepository.findAll();
        if (categories.isEmpty()) {
            log.warn("Список категорий пуст");
            return null;
        } else {
            return categories;
        }
    }

    public Category getCategoryById(Long id) {
        log.info("Получение категории по ID");
        Optional<Category> category = this.categoryRepository.findById(id);
        if (category.isPresent()) {
            return category.get();
        } else {
            log.error("Категория не найдена");
            return null;
        }
    }

    public Category createCategory(Category category) {
        log.info("Добавление новой категории");
        try {
            if (categoryRepository.existsBySlug(category.getSlug())) {
                log.warn("Категория уже существует");
                return categoryRepository.findBySlug(category.getSlug());
            } else {
                category.setNaviDate(LocalDateTime.now());
                category.setNaviUser(currentUser);
                Category createdCategory = categoryRepository.save(category);
                notifyObservers("CREATE", createdCategory);
                historyManager.addCategorySnapshot(new CategorySnapshot(createdCategory.getCategoryId(), createdCategory.getName(), createdCategory.getSlug()));
                return createdCategory;
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка добавления новой категории");
            throw new BadRequestException(e.getMessage());
        }
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        log.info("Обновление категории");
        try {
            Optional<Category> optionalCategory = categoryRepository.findById(id);
            if (optionalCategory.isPresent()) {
                Category existingCategory = optionalCategory.get();
                existingCategory.setNaviDate(LocalDateTime.now());
                existingCategory.setNaviUser(currentUser);
                existingCategory.setSlug(categoryDetails.getSlug());
                existingCategory.setName(categoryDetails.getName());
                Category updatedCategory = categoryRepository.save(existingCategory);
                notifyObservers("UPDATE", updatedCategory);
                historyManager.addCategorySnapshot(new CategorySnapshot(updatedCategory.getCategoryId(), updatedCategory.getName(), updatedCategory.getSlug()));
                return updatedCategory;
            } else {
                log.error("Категория не найдена");
                return null;
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка обновления категории");
            throw new BadRequestException(e.getMessage());
        }
    }

    public boolean deleteCategory(Long id) {
        log.info("Удаление категории");
        if (categoryRepository.existsById(id)) {
            Category categoryToDelete = categoryRepository.findById(id).orElse(null);
            categoryRepository.deleteById(id);
            notifyObservers("DELETE", categoryToDelete);
            historyManager.addCategorySnapshot(new CategorySnapshot(categoryToDelete.getCategoryId(), categoryToDelete.getName(), categoryToDelete.getSlug()));
            return true;
        } else {
            log.error("Событие не найдено");
            return false;
        }
    }
}
