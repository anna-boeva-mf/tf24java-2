package ru.tbank.patterns;

import lombok.Getter;

@Getter
public class CategorySnapshot {
    private Long categoryId;
    private String name;
    private String slug;

    public CategorySnapshot(Long categoryId, String name, String slug) {
        this.categoryId = categoryId;
        this.name = name;
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "CategorySnapshot{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
