package ru.tbank.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.entities.Category;
import ru.tbank.logging.LogExecutionTime;

@Slf4j
@Component
public class CategoryApiClient {
    @Value("${categories.url}")
    private String CATEGORIES_URL;
    private final RestTemplate restTemplate;

    public CategoryApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @LogExecutionTime
    public Category[] initializeData() {
        log.info("Загрузка категорий с ресурса kudago.com");
        Category[] categories = this.restTemplate.getForObject(this.CATEGORIES_URL, Category[].class);
        return categories;
    }
}
