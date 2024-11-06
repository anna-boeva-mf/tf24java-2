package ru.tbank.entities;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private int id;
    private String slug;
    private String name;

    public Category(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }

}
