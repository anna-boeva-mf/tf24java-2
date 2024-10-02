package ru.tbank.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.entities.Location;
import ru.tbank.logging.LogExecutionTime;

@Slf4j
@Component
public class LocationApiClient {
    @Value("${locations.url}")
    private String LOCATION_URL;
    private final RestTemplate restTemplate;

    public LocationApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @LogExecutionTime
    public Location[] initializeData() {
        log.info("Загрузка локаций с ресурса kudago.com");
        Location[] locations = this.restTemplate.getForObject(this.LOCATION_URL, Location[].class);
        return locations;
    }
}
