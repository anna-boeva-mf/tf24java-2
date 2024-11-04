package ru.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransientPropertyValueException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import ru.tbank.db_repository.EventRepository;
import ru.tbank.db_repository.EventSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.tbank.dto.EventDTO;
import ru.tbank.entities.Event;
import ru.tbank.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventService {

    @Value("${spring.datasource.username}")
    private String currentUser;

    @Autowired
    private EventRepository eventRepository;

    public List<EventDTO> getAllEvents() {
        log.info("Получение всех событий");
        List<Event> events = eventRepository.findAll();
        if (events.isEmpty()) {
            log.warn("Список событий пуст");
            return null;
        } else {
            List<EventDTO> eventsDTO = events.stream()
                    .map(event -> new EventDTO(event, true))
                    .collect(Collectors.toList());
            return eventsDTO;
        }
    }

    public EventDTO getEventById(Long id) {
        log.info("Получение события по ID");
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            return new EventDTO(event.get(), true);
        } else {
            log.error("Событие не найдено");
            return null;
        }
    }

    public EventDTO createEvent(Event event) {
        log.info("Создание нового события");
        try {
            if (eventRepository.existsBySlug(event.getSlug())) {
                log.warn("Событие уже существует");
                return new EventDTO(eventRepository.findBySlug(event.getSlug()), true);
            } else {
                event.setNaviDate(LocalDateTime.now());
                event.setNaviUser(currentUser);
                return new EventDTO(eventRepository.save(event), true);
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка добавления нового события");
            throw new BadRequestException(e.getMessage());
        } catch (InvalidDataAccessApiUsageException e) {
            log.error("Локация события не найдена");
            throw new BadRequestException(e.getMessage());
        }
    }

    public EventDTO updateEvent(Long id, Event eventDetails) {
        log.info("Обновление события");
        try {
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
                existingEvent.setNaviUser(eventDetails.getNaviUser());
                return new EventDTO(eventRepository.save(existingEvent), true);
            } else {
                log.error("Событие не найдено");
                return null;
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка обновления события");
            throw new BadRequestException(e.getMessage());
        }
    }

    public boolean deleteEvent(Long id) {
        log.info("Удаление события");
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        } else {
            log.error("Событие не найдено");
            return false;
        }
    }

    public List<EventDTO> searchEvents(String name, Long locationId, LocalDateTime fromDate, LocalDateTime toDate) {
        log.info("Поиск событий по фильтрам");
        Specification<Event> spec = Specification.where(EventSpecification.findByName(name))
                .and(EventSpecification.findByLocation(locationId))
                .and(EventSpecification.findByDateRange(fromDate, toDate));
        List<Event> events = eventRepository.findAll(spec);
        if (events.isEmpty()) {
            log.warn("Список событий пуст");
            return null;
        } else {
            List<EventDTO> eventsDTO = events.stream()
                    .map(event -> new EventDTO(event, true))
                    .collect(Collectors.toList());
            return eventsDTO;
        }
    }

}

