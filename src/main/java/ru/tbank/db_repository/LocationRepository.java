package ru.tbank.db_repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tbank.entities.Location;
import org.springframework.data.repository.query.Param;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("SELECT l FROM Location l LEFT JOIN FETCH l.events WHERE l.locationId = :id")
    Location findByIdWithEvents(@Param("id") Long id);

    Location findBySlug(String slug);

    boolean existsBySlug(String slug);
}
