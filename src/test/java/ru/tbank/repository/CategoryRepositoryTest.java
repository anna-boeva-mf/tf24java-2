package ru.tbank.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tbank.entities.Category;
import ru.tbank.exception.EntityNotFoundException;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRepositoryTest {

    private CategoryRepository categoryRepository;
    private Category category1;
    private Category category2;

    @BeforeEach
    public void setup() {
        categoryRepository = new CategoryRepository();
        category1 = new Category("animal-cafes", "Кафе с животными");
        category2 = new Category("stables", "Конюшни");
        categoryRepository.save(1, category1);
        categoryRepository.save(2, category2);
    }

    @Test
    void findById_Fine() {
        Category category = categoryRepository.findById(1);
        Assertions.assertAll(
                () -> Assertions.assertEquals("Кафе с животными", category.getName(), "Check Name from response"),
                () -> Assertions.assertEquals("animal-cafes", category.getSlug(), "Check Slug from response"));
    }

    @Test
    void saveTest_Fine() {
        Category category3 = new Category("new category", "NEW");
        Integer ID = categoryRepository.save(3, category3);
        assertThat(ID).isEqualTo(3);
    }

    @Test
    void delete_Fine() {
        categoryRepository.delete(1);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryRepository.findById(1);
        });
    }

    @Test
    void delete_nonexistent_Fine() {
        categoryRepository.delete(5);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryRepository.findById(5);
        });
    }

    @Test
    void findAll_Fine() {
        Collection<Category> categories = categoryRepository.findAll();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, categories.size(), "Check count from response"),
                () -> Assertions.assertEquals(true, categories.contains(category1), "Check category1 exists in response"),
                () -> Assertions.assertEquals(true, categories.contains(category2), "Check category2 exists in  response"));
    }

}