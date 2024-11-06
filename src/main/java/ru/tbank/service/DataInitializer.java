package ru.tbank.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import ru.tbank.repository.CategoryRepository;
import ru.tbank.client.CategoryApiClient;
import ru.tbank.client.LocationApiClient;
import ru.tbank.entities.Category;
import ru.tbank.entities.Location;
import ru.tbank.logging.LogExecutionTime;
import ru.tbank.repository.LocationRepository;

@Component
@Slf4j
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final CategoryApiClient categoryApiClient;
    private final CategoryRepository categoryRepository;
    private final LocationApiClient locationApiClient;
    private final LocationRepository locationRepository;

    @LogExecutionTime
    public void run(ApplicationArguments args) {
        log.info("Инициализация списка локаций");
        try {

            Location[] locations = this.locationApiClient.initializeData();
            for (Location location : locations) {
                int id = this.locationRepository.genId();
                location.setId(id);
                this.locationRepository.save(id, location);
            }
        } catch (RestClientException ex) {
            log.error("Ошибка загрузки списка локаций");
        }

        log.info("Инициализация списка категорий");
        try {
            Category[] categories = this.categoryApiClient.initializeData();
            for (Category category : categories) {
                int id = this.categoryRepository.genId();
                category.setId(id);
                this.categoryRepository.save(id, category);
            }
        } catch (RestClientException ex) {
            log.error("Ошибка загрузки списка категорий");
        }
    }
}
