package ru.tbank.patterns;

import lombok.Getter;

@Getter
public class EventSnapshot {
    private Long eventId;
    private String name;
    private String slug;

    public EventSnapshot(Long eventId, String name, String slug) {
        this.eventId = eventId;
        this.name = name;
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "EventSnapshot{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
