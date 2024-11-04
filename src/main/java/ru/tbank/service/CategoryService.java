package ru.tbank.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import ru.tbank.db_repository.CategoryRepository;
import ru.tbank.entities.Category;
import ru.tbank.exception.BadRequestException;

@Slf4j
@Service
public class CategoryService {

    @Value("${spring.datasource.username}")
    private String currentUser;

    @Autowired
    private CategoryRepository categoryRepository;

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
                return categoryRepository.save(category);
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
                return categoryRepository.save(existingCategory);
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
            categoryRepository.deleteById(id);
            return true;
        } else {
            log.error("Событие не найдено");
            return false;
        }
    }
}
