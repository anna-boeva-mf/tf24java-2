package ru.tbank.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "place_categories", schema = "events")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "slug", length = 100)
    private String slug;

    @Column(name = "navi_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime naviDate;

    @Column(name = "navi_user", length = 100, columnDefinition = "VARCHAR(100) DEFAULT CURRENT_USER")
    private String naviUser;


    public Category(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public Category(Long categoryId, String name, String slug) {
        this.categoryId = categoryId;
        this.name = name;
        this.slug = slug;
    }
}
