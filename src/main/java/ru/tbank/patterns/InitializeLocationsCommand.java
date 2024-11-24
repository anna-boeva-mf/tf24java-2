package ru.tbank.patterns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import ru.tbank.client.LocationApiClient;
import ru.tbank.db_repository.LocationRepository;
import ru.tbank.entities.Location;

import java.time.LocalDateTime;

@Component
@Slf4j
public class InitializeLocationsCommand implements Command {

    private final LocationApiClient locationApiClient;
    private final LocationRepository locationRepository;

    @Autowired
    public InitializeLocationsCommand(LocationApiClient locationApiClient, LocationRepository locationRepository) {
        this.locationApiClient = locationApiClient;
        this.locationRepository = locationRepository;
    }

    @Override
    public void execute() {
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
    }
}
