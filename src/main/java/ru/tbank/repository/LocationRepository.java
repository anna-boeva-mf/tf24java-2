package ru.tbank.repository;

import org.springframework.stereotype.Component;
import ru.tbank.entities.Location;

@Component
public class LocationRepository extends GenericRepository<Integer, Location>{
    private int counter = 1;

    public int genId() {
        return counter++;
    }
}
