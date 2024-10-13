package ru.tbank.entities;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
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

    public Category(int id, String slug, String name) {
        this.id = id;
        this.slug = slug;
        this.name = name;
    }
}
