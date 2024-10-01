package ru.tbank.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.tbank.GenericRepository;
import ru.tbank.clients.CategoryApiClient;
import ru.tbank.clients.LocationApiClient;
import ru.tbank.entities.Category;
import ru.tbank.entities.Location;
import ru.tbank.logging.LogExecutionTime;

@Component
public class DataInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final CategoryApiClient categoryApiClient;
    private final GenericRepository<Category> categoryRepository;
    private final LocationApiClient locationApiClient;
    private final GenericRepository<Location> locationRepository;

    @LogExecutionTime
    public void run(ApplicationArguments args) {
        log.info("Инициализируем данные");
        Category[] categories = this.categoryApiClient.initializeData();
        Category[] var3 = categories;
        int var4 = categories.length;

        int var5;
        for(var5 = 0; var5 < var4; ++var5) {
            Category category = var3[var5];
            int id = this.categoryRepository.genId();
            category.setId(id);
            this.categoryRepository.save(id, category);
        }

        Location[] locations = this.locationApiClient.initializeData();
        Location[] var10 = locations;
        var5 = locations.length;

        for(int var11 = 0; var11 < var5; ++var11) {
            Location location = var10[var11];
            int id = this.locationRepository.genId();
            location.setId(id);
            this.locationRepository.save(id, location);
        }

    }

    public DataInitializer(CategoryApiClient categoryApiClient, GenericRepository<Category> categoryRepository, LocationApiClient locationApiClient, GenericRepository<Location> locationRepository) {
        this.categoryApiClient = categoryApiClient;
        this.categoryRepository = categoryRepository;
        this.locationApiClient = locationApiClient;
        this.locationRepository = locationRepository;
    }
}
