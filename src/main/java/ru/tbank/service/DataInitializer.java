package ru.tbank.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import ru.tbank.client.CategoryApiClient;
import ru.tbank.client.EventApiClient;
import ru.tbank.client.LocationApiClient;
import ru.tbank.db_repository.CategoryRepository;
import ru.tbank.db_repository.EventRepository;
import ru.tbank.db_repository.LocationRepository;
import ru.tbank.entities.Category;
import ru.tbank.entities.DateRange;
import ru.tbank.entities.Event;
import ru.tbank.entities.Location;
import ru.tbank.logging.LogExecutionTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final CategoryApiClient categoryApiClient;
    private final CategoryRepository categoryRepository;
    private final LocationApiClient locationApiClient;
    private final LocationRepository locationRepository;
    private final EventApiClient eventApiClient;
    private final EventRepository eventRepository;

    @LogExecutionTime
    public void run(ApplicationArguments args) {
        log.info("Инициализация списка локаций");
        try {
            Location[] locations = this.locationApiClient.initializeData();
            for (Location location : locations) {
                if (locationRepository.existsBySlug(location.getSlug())) {
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
                if (categoryRepository.existsBySlug(category.getSlug())) {
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

        // Добавлена загрузка нескольких реальных событий, для полноты исходного состояния хранилища
        log.info("Инициализация списка событий");
        try {
            List <Event> events = this.eventApiClient.initializeData();
            for (Event event : events) {
                if (eventRepository.existsBySlug(event.getSlug())) {
                    log.warn("Event already exists");
                } else {
                    String currentUser = "Initializer";
                    Location responseEventLocation = event.getLocation();
                    Location eventLocation = locationRepository.findBySlug(responseEventLocation.getSlug());
                    if (eventLocation == null) { //в апи локаций не полный список реальных значений, сначала добавить новую локацию
                        eventLocation = responseEventLocation;
                        eventLocation.setNaviDate(LocalDateTime.now());
                        eventLocation.setNaviUser(currentUser);
                        this.locationRepository.save(eventLocation);
                    }
                    event.setLocation(eventLocation);
                    List<DateRange> dates = event.getDates();
                    dates.sort(Comparator.comparing(DateRange::getEnd).reversed()); // в списке самая свежая пара дат
                    LocalDateTime eventStartDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(dates.get(0).getStart()), ZoneId.systemDefault());
                    LocalDateTime eventEndDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(dates.get(0).getEnd()), ZoneId.systemDefault());
                    event.setStartDate(eventStartDate);
                    event.setEndDate(eventEndDate);
                    event.setNaviDate(LocalDateTime.now());
                    event.setNaviUser(currentUser);
                    this.eventRepository.save(event);
                }
            }
        } catch (RestClientException ex) {
            log.error("Ошибка загрузки списка событий");
        }

    }
}
