package ru.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.stereotype.Service;
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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataInitializationService implements ApplicationListener<ApplicationStartedEvent> {
    private final CategoryApiClient categoryApiClient;
    private final CategoryRepository categoryRepository;
    private final LocationApiClient locationApiClient;
    private final LocationRepository locationRepository;
    private final EventApiClient eventApiClient;
    private final EventRepository eventRepository;

    private final ExecutorService fixedThreadPool;
    private final ExecutorService scheduledThreadPool;
    private final Duration initializationInterval;

    @Autowired
    public DataInitializationService(
            CategoryApiClient categoryApiClient, CategoryRepository categoryRepository, LocationApiClient locationApiClient, LocationRepository locationRepository, EventApiClient eventApiClient, EventRepository eventRepository, @Qualifier("fixedThreadPool") ExecutorService fixedThreadPool,
            @Qualifier("scheduledThreadPool") ExecutorService scheduledThreadPool,
            @Value("${initialization.interval}") Duration initializationInterval) {
        this.categoryApiClient = categoryApiClient;
        this.categoryRepository = categoryRepository;
        this.locationApiClient = locationApiClient;
        this.locationRepository = locationRepository;
        this.eventApiClient = eventApiClient;
        this.eventRepository = eventRepository;
        this.fixedThreadPool = fixedThreadPool;
        this.scheduledThreadPool = scheduledThreadPool;
        this.initializationInterval = initializationInterval;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        scheduledThreadPool.submit(() -> {
            while (true) {
                try {
                    initializeData();
                    Thread.sleep(initializationInterval.toMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void initializeData() {
        long startTime = System.currentTimeMillis();

        log.info("Инициализация списка локаций");
        try {
            List<Location> locations = this.locationApiClient.initializeData();
            List<CompletableFuture<Void>> futures = locations.stream()
                    .map(location -> CompletableFuture.runAsync(() -> {
                        if (locationRepository.existsBySlug(location.getSlug())) {
                            log.warn("Location already exists");
                        } else {
                            location.setNaviDate(LocalDateTime.now());
                            String currentUser = "Initializer";
                            location.setNaviUser(currentUser);
                            this.locationRepository.save(location);
                        }
                    }, fixedThreadPool))
                    .collect(Collectors.toList());
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();
        } catch (RestClientException ex) {
            log.error("Ошибка загрузки списка локаций");
        }

        log.info("Инициализация списка категорий");
        try {
            List<Category> categories = this.categoryApiClient.initializeData();
            List<CompletableFuture<Void>> futures = categories.stream()
                    .map(category -> CompletableFuture.runAsync(() -> {
                        if (categoryRepository.existsBySlug(category.getSlug())) {
                            log.warn("Category already exists");
                        } else {
                            category.setNaviDate(LocalDateTime.now());
                            String currentUser = "Initializer";
                            category.setNaviUser(currentUser);
                            this.categoryRepository.save(category);
                        }
                    }, fixedThreadPool))
                    .collect(Collectors.toList());
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();
        } catch (RestClientException ex) {
            log.error("Ошибка загрузки списка категорий");
        }

        log.info("Инициализация списка событий");
        try {
            List<Event> events = this.eventApiClient.initializeData();
            List<CompletableFuture<Void>> futures = events.stream()
                    .map(event -> CompletableFuture.runAsync(() -> {
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
                    }, fixedThreadPool))
                    .collect(Collectors.toList());
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();
        } catch (RestClientException ex) {
            log.error("Ошибка загрузки списка событий");
        }
        long endTime = System.currentTimeMillis();
        log.info("Инициализация данных произведена за {} мс", (endTime - startTime));
    }
}
