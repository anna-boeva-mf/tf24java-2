package ru.tbank.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.tbank.GenericRepository;
import ru.tbank.logging.LogExecutionTime;
import ru.tbank.entities.Category;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places/categories")
public class CategoryController {
    private final GenericRepository<Category> categoryRepository = new GenericRepository<>();

    private static int counter = 1;

    public int genId() {
        return counter++;
    }

    public CategoryController() {
        initializeData();
    }

    @LogExecutionTime
    private void initializeData() {
        RestTemplate restTemplate = new RestTemplate();

        String categoriesUrl = "https://kudago.com/public-api/v1.4/place-categories/";
        Category[] categories = restTemplate.getForObject(categoriesUrl, Category[].class);
        assert categories != null;
        for (Category category : categories) {
            int Id = genId();
            category.setId(Id);
            categoryRepository.save(Id, category);
        }
    }

    @LogExecutionTime
    @GetMapping
    public List<Category> getAllCategories() {
        return List.copyOf(categoryRepository.findAll());
    }

    @LogExecutionTime
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable int id) {
        return categoryRepository.findById(id);
    }

    @LogExecutionTime
    @PostMapping
    public void createCategory(@RequestBody Category category) {
        int Id = genId();
        category.setId(Id);
        categoryRepository.save(Id, category);
    }

    @LogExecutionTime
    @PutMapping("/{id}")
    public void updateCategory(@PathVariable int id, @RequestBody Category category) {
        category.setId(id);
        categoryRepository.save(id, category);
    }

    @LogExecutionTime
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable int id) {
        categoryRepository.delete(id);
    }
}
