package ru.tbank.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import ru.tbank.entities.Location;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDTO {
    private Long locationId;
    private String name;
    private String slug;
    private List<EventDTO> events;

    public LocationDTO(Location location, boolean includeEvents) {
        this.locationId = location.getLocationId();
        this.name = location.getName();
        this.slug = location.getSlug();
        if (includeEvents && location.getEvents() != null) {
            List<EventDTO> eventsDTO = location.getEvents().stream()
                    .map(event -> new EventDTO(event, false))
                    .collect(Collectors.toList());
            this.events = eventsDTO;
        }
    }


}

