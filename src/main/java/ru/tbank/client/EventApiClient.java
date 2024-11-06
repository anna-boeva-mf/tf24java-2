package ru.tbank.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.entities.Event;
import ru.tbank.entities.EventResponse;
import ru.tbank.logging.LogExecutionTime;

import java.time.ZonedDateTime;

@Slf4j
@Component
public class EventApiClient {
    @Value("${events.url}")
    private String EVENTS_URL;
    private final RestTemplate restTemplate;

    public EventApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @LogExecutionTime
    public Event[] initializeData() {
        log.info("Загрузка категорий с ресурса kudago.com");
        long actuaSince = ZonedDateTime.now().minusHours(3).toEpochSecond();
        long actuaUntil = ZonedDateTime.now().toEpochSecond();
        String URL = this.EVENTS_URL + "/?fields=title,slug,location,site_url,dates&actual_since=" + actuaSince + "&actual_until=" + actuaUntil;
        EventResponse eventResponse = this.restTemplate.getForObject(URL, EventResponse.class);
        return eventResponse != null ? eventResponse.getResults() : new Event[0];
    }
}
