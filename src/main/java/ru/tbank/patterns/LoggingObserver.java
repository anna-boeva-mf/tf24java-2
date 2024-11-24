package ru.tbank.patterns;

import ru.tbank.dto.LocationDTO;
import ru.tbank.entities.Category;
import ru.tbank.dto.EventDTO;

public class LoggingObserver implements Observer {
    @Override
    public void update(String action, Object entity) {
        if (entity instanceof Category) {
            Category category = (Category) entity;
            System.out.println("Action: " + action + ", Category: " + category);
        } else if (entity instanceof LocationDTO) {
            LocationDTO location = (LocationDTO) entity;
            System.out.println("Action: " + action + ", Location: " + location);
        } else if (entity instanceof EventDTO) {
            EventDTO event = (EventDTO) entity;
            System.out.println("Action: " + action + ", Event: " + event);
        }
    }
}
