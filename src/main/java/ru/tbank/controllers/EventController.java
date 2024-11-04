package ru.tbank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.dto.EventDTO;
import ru.tbank.entities.Event;
import ru.tbank.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> eventsDTO = eventService.getAllEvents();
        return new ResponseEntity<>(eventsDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        EventDTO eventsDTO = eventService.getEventById(id);
        if (eventsDTO != null) {
            return new ResponseEntity<>(eventsDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody Event event) {
        EventDTO createdEventDTO= eventService.createEvent(event);
        return new ResponseEntity<>(createdEventDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        EventDTO updatedEventDTO = eventService.updateEvent(id, eventDetails);
        if (updatedEventDTO != null) {
            return new ResponseEntity<>(updatedEventDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        boolean isDeleted = eventService.deleteEvent(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public List<EventDTO> searchEvents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return eventService.searchEvents(name, locationId, fromDate, toDate);
    }
}

