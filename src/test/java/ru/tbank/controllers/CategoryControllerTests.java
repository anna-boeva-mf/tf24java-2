package ru.tbank.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.entities.Category;
import ru.tbank.service.CategoryService;


import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CategoryController.class)
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    public void testGetAll() throws Exception {
        //Arrange
        Category category1 = new Category(1, "a", "A");
        Category category2 = new Category(2, "b", "B");
        when(categoryService.getAllCategories()).thenReturn(List.of(category1, category2));

        //Act
        mockMvc.perform(get("/api/v1/places/categories"))
                //Assert
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            /*    .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories.length()").value(2))
                .andExpect(jsonPath("$.categories[0].id").value(category1.getId()))
                .andExpect(jsonPath("$.categories[0].slug").value(category1.getSlug()))
                .andExpect(jsonPath("$.categories[0].name").value(category1.getName()))
                .andExpect(jsonPath("$.categories[1].id").value(category2.getId()))
                .andExpect(jsonPath("$.categories[1].slug").value(category2.getSlug()))
                .andExpect(jsonPath("$.categories[1].name").value(category2.getName()))*/;
    }
}