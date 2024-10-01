package ru.tbank.services;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tbank.GenericRepository;
import ru.tbank.entities.Location;

@Service
public class LocationService {
    private static final Logger log = LoggerFactory.getLogger(LocationService.class);
    private final GenericRepository<Location> locationRepository;

    public Collection<Location> getAllLocations() {
        log.info("Получение всех локаций");
        return this.locationRepository.findAll();
    }

    public Location getLocationById(int id) {
        return (Location)this.locationRepository.findById(id);
    }

    public int createLocation(Location location) {
        int id = this.locationRepository.genId();
        location.setId(id);
        return this.locationRepository.save(id, location);
    }

    public void updateLocation(int id, Location location) {
        location.setId(id);
        this.locationRepository.save(id, location);
    }

    public void deleteLocation(int id) {
        this.locationRepository.delete(id);
    }

    public LocationService(GenericRepository<Location> locationRepository) {
        this.locationRepository = locationRepository;
    }
}
