package ru.tbank.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.tbank.entities.Category;
import ru.tbank.exception.EntityNotFoundException;
import ru.tbank.repository.CategoryRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


class CategoryServiceTest {

    private CategoryRepository categoryRepository = new CategoryRepository();
    private CategoryService categoryService = new CategoryService(categoryRepository);
    private Category category1 = new Category("animal-cafes", "Кафе с животными");
    private Category category2 = new Category("stables", "Конюшни");

    @Test
    void getCategoryById() {
        categoryService.createCategory(category1);
        Category returnCategory = categoryService.getCategoryById(1);
        assertThat(returnCategory.getId()).isEqualTo(category1.getId());
    }

    @Test
    void getAllCategories() {
        categoryService.createCategory(category1);
        categoryService.createCategory(category2);
        Collection<Category> categories = categoryService.getAllCategories();
        Assertions.assertEquals(2, categories.size());
    }

    @Test
    void updateCategory() {
        categoryService.createCategory(category2);
        categoryService.updateCategory(2, category1);
        Category returnCategory = categoryService.getCategoryById(2);
        Assertions.assertAll(
                () -> Assertions.assertEquals("Кафе с животными", returnCategory.getName(), "Check Name from response"),
                () -> Assertions.assertEquals("animal-cafes", returnCategory.getSlug(), "Check Slug from response"));
    }


    @Test
    void deleteCategory() {
        categoryService.createCategory(category1);
        categoryService.deleteCategory(1);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.getCategoryById(1);
        });
    }

}