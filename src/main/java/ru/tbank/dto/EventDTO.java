package ru.tbank.dto;

import lombok.Getter;
import lombok.Setter;
import ru.tbank.entities.Event;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDTO {
    private Long eventId;
    private String name;
    private String slug;
    private String siteUrl;
    private Long locationId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public EventDTO(Event event, boolean includeLocation) {
        this.eventId = event.getEventId();
        this.name = event.getName();
        this.slug = event.getSlug();
        this.siteUrl = event.getSiteUrl();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        if (includeLocation && event.getLocation() != null) {
            this.locationId = event.getLocation().getLocationId();
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + eventId +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
