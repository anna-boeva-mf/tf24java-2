package ru.tbank.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import ru.tbank.db_repository.CategoryRepository;
import ru.tbank.entities.Category;
import ru.tbank.service.CategoryService;

import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    public static PostgreSQLContainer<?> pgDB = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("kudago_test")
            .withUsername("pguser_test")
            .withPassword("pgpwd_test");

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", pgDB::getJdbcUrl);
        registry.add("spring.datasource.username", pgDB::getUsername);
        registry.add("spring.datasource.password", pgDB::getPassword);
    }

    @ParameterizedTest
    @ValueSource(strings = {"airports", "amusement", "animal-shelters", "photo-places", "dogs", "cats", "dance-studio"})
    public void testGetAllCategories_LoadedSlugsInInitializer(String expectedSlug) throws Exception {
        mockMvc.perform(get("/api/v1/places/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].slug").value(hasItem(expectedSlug)))
                .andDo(print());
    }

    @Test
    public void testGetCategoryById_LoadedByInitializer() throws Exception {
        mockMvc.perform(get("/api/v1/places/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.naviUser").value("Initializer"))
                .andDo(print());
    }

    @Test
    public void testAddCategory_WasCreated() throws Exception {
        MvcResult beforResult = mockMvc.perform(get("/api/v1/places/categories"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonBeforResult = new String(beforResult.getResponse().getContentAsByteArray());
        int beforResultCount = objectMapper.readValue(jsonBeforResult, new TypeReference<List<Category>>() {
        }).size();

        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"main\", \"name\": \"MAIN CATEGORY\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        MvcResult afterResult = mockMvc.perform(get("/api/v1/places/categories"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonAfterResult = new String(afterResult.getResponse().getContentAsByteArray());
        int afterResultCount = objectMapper.readValue(jsonAfterResult, new TypeReference<List<Category>>() {
        }).size();

        Assertions.assertAll(
                () -> Assertions.assertEquals(beforResultCount + 1, afterResultCount, "Количество категорий увеличилось"),
                () -> Assertions.assertTrue(categoryRepository.existsBySlug("main"), "Новая категория добавилась"),
                () -> Assertions.assertEquals(afterResultCount, categoryRepository.findBySlug("main").getCategoryId(), "ИД новой категории"),
                () -> Assertions.assertEquals("MAIN CATEGORY", categoryRepository.findBySlug("main").getName(), "Наименование новой категории"));
    }

    @Test
    public void testUpdateCategory_WasUpdated() throws Exception {
        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"main\", \"name\": \"MAIN CATEGORY\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
        Category categoryBefore = categoryRepository.findBySlug("main");
        Long categoryId = categoryBefore.getCategoryId();

        mockMvc.perform(put("/api/v1/places/categories/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"std\", \"name\": \"STADIUM\"}"))
                .andExpect(status().isOk())
                .andDo(print());

        Category categoryAfter = categoryRepository.findBySlug("std");
        Assertions.assertAll(
                () -> Assertions.assertEquals(categoryId, categoryAfter.getCategoryId(), "ИД обновленной категории"),
                () -> Assertions.assertEquals("STADIUM", categoryAfter.getName(), "Наименование обновленной категории"));
    }

    @Test
    public void testDeleteCategory_WasDeleted() throws Exception {
        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"main\", \"name\": \"MAIN CATEGORY\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
        Category categoryBefore = categoryRepository.findBySlug("main");
        Long categoryId = categoryBefore.getCategoryId();

        mockMvc.perform(delete("/api/v1/places/categories/" + categoryId))
                .andExpect(status().isNoContent())
                .andDo(print());

        mockMvc.perform(get("/api/v1/places/categories/" + categoryId))
                .andExpect(status().isNotFound())
                .andDo(print());

        Category categoryAfterDeleting = categoryRepository.findBySlug("main");

        Assertions.assertTrue(categoryAfterDeleting == null);
    }
}
