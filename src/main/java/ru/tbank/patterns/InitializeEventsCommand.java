package ru.tbank.patterns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import ru.tbank.entities.DateRange;
import ru.tbank.entities.Event;
import ru.tbank.entities.Location;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import ru.tbank.client.EventApiClient;
import ru.tbank.db_repository.EventRepository;
import ru.tbank.db_repository.LocationRepository;

@Component
@Slf4j
public class InitializeEventsCommand implements Command {

    private final EventApiClient eventApiClient;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public InitializeEventsCommand(EventApiClient eventApiClient, EventRepository eventRepository, LocationRepository locationRepository) {
        this.eventApiClient = eventApiClient;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public void execute() {
        log.info("Инициализация списка событий");
        try {
            List<Event> events = this.eventApiClient.initializeData();
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
