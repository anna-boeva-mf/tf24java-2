package ru.tbank.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import ru.tbank.client.CategoryApiClient;
import ru.tbank.client.LocationApiClient;
import ru.tbank.db_repository.CategoryRepository;
import ru.tbank.db_repository.LocationRepository;
import ru.tbank.entities.Category;
import ru.tbank.entities.Location;
import ru.tbank.logging.LogExecutionTime;

import java.time.LocalDateTime;

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
                if (locationRepository.existsByName(location.getName())) {
                    log.warn("Location already exists");
                } else {
                    location.setNaviDate(LocalDateTime.now());
                    String currentUser = "Initializer";
                    location.setNaviUser(currentUser);
                    this.locationRepository.save(location);
                }
            }
        } catch (RestClientException ex) {
            log.error("Ошибка загрузки списка локаций");
        }

        log.info("Инициализация списка категорий");
        try {
            Category[] categories = this.categoryApiClient.initializeData();
            for (Category category : categories) {
                if (categoryRepository.existsByName(category.getName())) {
                    log.warn("Category already exists");
                } else {
                    category.setNaviDate(LocalDateTime.now());
                    String currentUser = "Initializer";
                    category.setNaviUser(currentUser);
                    this.categoryRepository.save(category);
                }
            }
        } catch (RestClientException ex) {
            log.error("Ошибка загрузки списка категорий");
        }
    }
}
