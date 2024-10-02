package ru.tbank.controllers;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.entities.Category;
import ru.tbank.logging.LogExecutionTime;
import ru.tbank.service.CategoryService;

@Slf4j
@RestController
@RequestMapping({"/api/v1/places/categories"})
@LogExecutionTime
@AllArgsConstructor
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;

    @GetMapping
    public Collection<Category> getAllCategories() {
        return this.categoryService.getAllCategories();
    }

    @LogExecutionTime
    @GetMapping({"/{id}"})
    public Category getCategoryById(@PathVariable int id) {
        return this.categoryService.getCategoryById(id);
    }

    @LogExecutionTime
    @PostMapping
    public void createCategory(@RequestBody Category category) {
        this.categoryService.createCategory(category);
    }

    @LogExecutionTime
    @PutMapping({"/{id}"})
    public void updateCategory(@PathVariable int id, @RequestBody Category category) {
        this.categoryService.updateCategory(id, category);
    }

    @LogExecutionTime
    @DeleteMapping({"/{id}"})
    public void deleteCategory(@PathVariable int id) {
        this.categoryService.deleteCategory(id);
    }
}
