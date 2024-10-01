package ru.tbank.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.entities.Category;
import ru.tbank.logging.LogExecutionTime;

@Component
public class CategoryApiClient {
    private static final Logger log = LoggerFactory.getLogger(CategoryApiClient.class);
    @Value("${categories.url}")
    private String CATEGORIES_URL;
    private final RestTemplate restTemplate;

    @LogExecutionTime
    public Category[] initializeData() {
        log.info("Загрузка категорий с KudaGo");
        Category[] categories = (Category[])this.restTemplate.getForObject(this.CATEGORIES_URL, Category[].class, new Object[0]);

        assert categories != null;

        return categories;
    }

    public CategoryApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}