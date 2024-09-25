package ru.tbank.entities;

import lombok.Data;

@Data
public class Category {
    private int id;
    private String slug;
    private String name;

    public Category() {
    }

    public Category(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }
}
