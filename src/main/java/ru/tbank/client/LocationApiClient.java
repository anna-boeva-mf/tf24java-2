package ru.tbank.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.entities.Location;
import ru.tbank.logging.LogExecutionTime;

@Slf4j
@Component
public class LocationApiClient {
    private final ClientProperties clientProperties;
    private final RestTemplate restTemplate;

    public LocationApiClient(ClientProperties clientProperties, RestTemplate restTemplate) {
        this.clientProperties = clientProperties;
        this.restTemplate = restTemplate;
    }

    @LogExecutionTime
    public Location[] initializeData() {
        log.info("Загрузка локаций с ресурса kudago.com");
        String locationsUrl = clientProperties.getLocationsUrl();
        Location[] locations = this.restTemplate.getForObject(locationsUrl, Location[].class);
        return locations;
    }
}
