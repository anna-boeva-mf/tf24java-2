package ru.tbank.patterns;

import lombok.Getter;

@Getter
public class LocationSnapshot {
    private Long locationId;
    private String name;
    private String slug;

    public LocationSnapshot(Long locationId, String name, String slug) {
        this.locationId = locationId;
        this.name = name;
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "LocationSnapshot{" +
                "locationId=" + locationId +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
