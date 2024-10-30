package ru.tbank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.entities.Category;
import ru.tbank.exception.EntityNotFoundException;
import ru.tbank.service.CategoryService;


import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({CategoryController.class})
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetCategories_FineResult() throws Exception {

        Category category1 = new Category("main", "MAIN CATEGORY");
        Category category2 = new Category("std", "STADIUM");
        Category category3 = new Category("arn", "ARENA");
        List<Category> t = List.of(category1, category2, category3);
        when(categoryService.getAllCategories()).thenReturn(t);

        mockMvc.perform(get("/api/v1/places/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(category1.getId()))
                .andExpect(jsonPath("$[1].slug").value(category2.getSlug()))
                .andExpect(jsonPath("$[2].name").value(category3.getName()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(t)))
                .andDo(print());
    }

    @Test
    public void testGetCategory_FineResult() throws Exception {

        Category category1 = new Category(1,"main", "MAIN CATEGORY");
        when(categoryService.getCategoryById(1)).thenReturn(category1);

        mockMvc.perform(get("/api/v1/places/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category1.getId()))
                .andExpect(jsonPath("$.slug").value(category1.getSlug()))
                .andExpect(jsonPath("$.name").value(category1.getName()))
                .andDo(print());
    }

    @Test
    public void testAddCategory_FineResult() throws Exception {
        Category category1 = new Category("main", "MAIN CATEGORY");
        when(categoryService.createCategory(category1)).thenReturn(1);

        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"main\", \"name\": \"MAIN CATEGORY\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testDeleteCategory_FineResult() throws Exception {
        Category category1 = new Category(1,"main", "MAIN CATEGORY");
        categoryService.createCategory(category1);

        mockMvc.perform(delete("/api/v1/places/categories/1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testDeleteCategory_WithNoEntity() throws Exception {
        Category category1 = new Category(1,"main", "MAIN CATEGORY");
        categoryService.createCategory(category1);

        mockMvc.perform(delete("/api/v1/places/categories/8888888"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testUpdateCategory_FineResult() throws Exception {
        Category category1 = new Category("main", "MAIN CATEGORY");
        categoryService.createCategory(category1);

        mockMvc.perform(put("/api/v1/places/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"main\", \"name\": \"MAIN CATEGORY\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetCategories_BadResult_WrongPath() throws Exception {
        List<Category> t = null;
        when(categoryService.getAllCategories()).thenReturn(t);

        mockMvc.perform(get("/api/v1/places/categoriesSSSSSS"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetCategory_BadResult_DoesntExist() throws Exception {

        when(categoryService.getCategoryById(111)).thenThrow(new EntityNotFoundException("Сущность с ID=111 не найдена"));
     //   when(categoryService.getCategoryById(111)).thenThrow(new Exception("Сущность с ID=111 не найдена"));

        mockMvc.perform(get("/api/v1/places/categories/111"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
