package ru.tbank.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.entities.Location;
import ru.tbank.logging.LogExecutionTime;

@Component
public class LocationApiClient {
    private static final Logger log = LoggerFactory.getLogger(LocationApiClient.class);
    @Value("${locations.url}")
    private String LOCATION_URL;
    private final RestTemplate restTemplate;

    @LogExecutionTime
    public Location[] initializeData() {
        log.info("Загрузка локаций с KudaGo");
        Location[] locations = (Location[])this.restTemplate.getForObject(this.LOCATION_URL, Location[].class, new Object[0]);
        return locations;
    }

    public LocationApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}