package ru.tbank.entities;

import lombok.Data;

@Data
public class Location {
    private int id;
    private String slug;
    private String name;

    public Location() {
    }

    public Location(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }
}
