package ru.tbank.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.tbank.logging.LogExecutionTime;
import ru.tbank.entities.Category;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places/categories")
public class CategoryController {
    private final GenericRepository<Category> categoryRepository = new GenericRepository<>();

    public CategoryController() {
        initializeData();
    }

    @LogExecutionTime
    private void initializeData() {
        RestTemplate restTemplate = new RestTemplate();

        String categoriesUrl = "https://kudago.com/public-api/v1.4/place-categories/";
        Category[] categories = restTemplate.getForObject(categoriesUrl, Category[].class);
        for (Category category : categories) {
            categoryRepository.save(category.getSlug(), category);
            //  categoryRepository.save(category.getSlug(), category);
        }
    }


    @LogExecutionTime
    @GetMapping
    public List<Category> getAllCategories() {
        return List.copyOf(categoryRepository.findAll());
    }

    @LogExecutionTime
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable String id) {
        return categoryRepository.findById(id);
    }

    @LogExecutionTime
    @PostMapping
    public void createCategory(@RequestBody Category category) {
        categoryRepository.save(category.getSlug(), category);
    }

    @LogExecutionTime
    @PutMapping("/{id}")
    public void updateCategory(@PathVariable String id, @RequestBody Category category) {
        categoryRepository.save(id, category);
    }

    @LogExecutionTime
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable String id) {
        categoryRepository.delete(id);
    }
}