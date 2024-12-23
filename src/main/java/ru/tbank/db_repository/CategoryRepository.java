package ru.tbank.db_repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsBySlug(String slug);

    Category findBySlug(String slug);
}
