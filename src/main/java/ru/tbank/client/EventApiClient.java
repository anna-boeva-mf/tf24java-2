package ru.tbank.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.entities.Event;
import ru.tbank.entities.EventResponse;
import ru.tbank.logging.LogExecutionTime;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Component
public class EventApiClient {
    private final ClientProperties clientProperties;
    private final RestTemplate restTemplate;

    public EventApiClient(ClientProperties сlientProperties, RestTemplate restTemplate) {
        this.clientProperties = сlientProperties;
        this.restTemplate = restTemplate;
    }

    @LogExecutionTime
    public List<Event> initializeData() {
        log.info("Загрузка категорий с ресурса kudago.com");
        long actuaSince = ZonedDateTime.now().minusHours(3).toEpochSecond();
        long actuaUntil = ZonedDateTime.now().toEpochSecond();
        String eventsUrl = clientProperties.getEventsUrl();
        String URL = eventsUrl + "/?fields=title,slug,location,site_url,dates&actual_since=" + actuaSince + "&actual_until=" + actuaUntil;
        EventResponse eventResponse = this.restTemplate.getForObject(URL, EventResponse.class);
        return eventResponse != null ? eventResponse.getResults() : List.of(new Event[0]);
    }
}
