package ru.tbank.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tbank.db_repository.CategoryRepository;
import ru.tbank.entities.Category;

@Slf4j
@Service
public class CategoryService {

    @Value("${spring.datasource.username}")
    private String currentUser;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        log.info("Получение всех категорий");
        return this.categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        log.info("Получение категории по ID");
        return this.categoryRepository.findById(id).orElse(null);
    }

    public Category createCategory(Category category) {
        log.info("Добавление новой категории");
        if (categoryRepository.existsByName(category.getName())) {
            log.warn("Category already exists");
            return categoryRepository.findByName(category.getName());
        } else {
            category.setNaviDate(LocalDateTime.now());
            category.setNaviUser(currentUser);
            return categoryRepository.save(category);
        }
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        log.info("Обновление категории");
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category existingCategory = optionalCategory.get();
            existingCategory.setNaviDate(LocalDateTime.now());
            existingCategory.setNaviUser(currentUser);
            existingCategory.setSlug(categoryDetails.getSlug());
            existingCategory.setName(categoryDetails.getName());
            return categoryRepository.save(existingCategory);
        }
        return null;
    }

    public boolean deleteCategory(Long id) {
        log.info("Удаление категории");
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
