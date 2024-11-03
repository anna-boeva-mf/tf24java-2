package ru.tbank.service;

import lombok.extern.slf4j.Slf4j;
import ru.tbank.db_repository.EventRepository;
import ru.tbank.db_repository.EventSpecification;
import ru.tbank.db_repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.tbank.entities.Event;
import ru.tbank.entities.Location;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    public List<Event> getAllEvents() {
        log.info("Получение всех событий");
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        log.info("Получение события по ID");
        return eventRepository.findById(id).orElse(null);
    }

    public Event createEvent(Event event) {
        log.info("Создание нового события");
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        log.info("Обновление события");
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isPresent()) {
            Event existingEvent = optionalEvent.get();
            existingEvent.setName(eventDetails.getName());
            existingEvent.setSlug(eventDetails.getSlug());
            existingEvent.setSiteUrl(eventDetails.getSiteUrl());
            existingEvent.setStartDate(eventDetails.getStartDate());
            existingEvent.setEndDate(eventDetails.getEndDate());
            existingEvent.setLocation(eventDetails.getLocation());
            existingEvent.setNaviDate(eventDetails.getNaviDate());
            existingEvent.setNaviUser (eventDetails.getNaviUser ());
            return eventRepository.save(existingEvent);
        }
        return null;
    }

    public boolean deleteEvent(Long id) {
        log.info("Удаление события");
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Event> searchEvents(String name, Long locationId, LocalDateTime fromDate, LocalDateTime toDate) {
        log.info("Поиск событий по фильтрам");
        Specification<Event> spec = Specification.where(EventSpecification.findByName(name))
                .and(EventSpecification.findByLocation(locationId))
                .and(EventSpecification.findByDateRange(fromDate, toDate));
        return eventRepository.findAll(spec);
    }

    public Location getLocationWithEvents(Long id) {
        log.info("Получение локации с событиями");
        return locationRepository.findByIdWithEvents(id);
    }
}

