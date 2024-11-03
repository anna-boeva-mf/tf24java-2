package ru.tbank.db_repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tbank.entities.Location;

import java.time.LocalDateTime;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    boolean existsByName(String name);

    Location findByName(String name);

    @Query("SELECT l FROM Location l LEFT JOIN FETCH l.events WHERE l.locationId = :id")
    Location findByIdWithEvents(Long id);
}
