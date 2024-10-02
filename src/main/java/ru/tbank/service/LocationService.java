package ru.tbank.service;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.entities.Location;
import ru.tbank.repository.LocationRepository;

@Slf4j
@Service
public class LocationService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Collection<Location> getAllLocations() {
        log.info("Получение всех локаций");
        return this.locationRepository.findAll();
    }

    public Location getLocationById(int id) {
        log.info("Получение локации по ID");
        return this.locationRepository.findById(id);
    }

    public int createLocation(Location location) {
        log.info("Создание новой локации");
        int id = this.locationRepository.genId();
        location.setId(id);
        return this.locationRepository.save(id, location);
    }

    public void updateLocation(int id, Location location) {
        log.info("Обновление локации");
        location.setId(id);
        this.locationRepository.save(id, location);
    }

    public void deleteLocation(int id) {
        log.info("Удаление локации");
        this.locationRepository.delete(id);
    }
}
