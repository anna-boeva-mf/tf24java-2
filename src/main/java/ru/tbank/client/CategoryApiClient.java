package ru.tbank.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.entities.Category;
import ru.tbank.logging.LogExecutionTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class CategoryApiClient {
    private final ClientProperties clientProperties;
    private final RestTemplate restTemplate;

    public CategoryApiClient(ClientProperties clientProperties, RestTemplate restTemplate) {
        this.clientProperties = clientProperties;
        this.restTemplate = restTemplate;
    }

    @LogExecutionTime
    public List<Category> initializeData() {
        log.info("Загрузка категорий с ресурса kudago.com");
        String categoriesUrl = clientProperties.getCategoriesUrl();
        return Stream.of(this.restTemplate.getForObject(categoriesUrl, Category[].class))
                .collect(Collectors.toList());
    }
}
