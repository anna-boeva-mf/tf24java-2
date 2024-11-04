package ru.tbank.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tbank.db_repository.LocationRepository;
import ru.tbank.entities.Location;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LocationService {

    @Value("${spring.datasource.username}")
    private String currentUser;

    @Autowired
    private LocationRepository locationRepository;


    public List<Location> getAllLocations() {
        log.info("Получение всех локаций");
        return locationRepository.findAll();
    }

    public Location getLocationById(Long id) {
        log.info("Получение локации по ID");
        return locationRepository.findById(id).orElse(null);
    }

    public Location getLocationWithEvents(Long id) {
        log.info("Получение локации с событиями");
        return locationRepository.findByIdWithEvents(id);
    }

    public Location createLocation(Location location) {
        log.info("Создание локации");
        if (locationRepository.existsByName(location.getName())) {
            log.warn("Location already exists");
            return locationRepository.findByName(location.getName());
        } else {
            location.setNaviDate(LocalDateTime.now());
            location.setNaviUser(currentUser);
            return locationRepository.save(location);
        }
    }

    public Location updateLocation(Long id, Location locationDetails) {
        log.info("Обновление локации");
        Location existingLocation = locationRepository.findByIdWithEvents(id);
        if (existingLocation == null) {
            log.warn("Локация не существует");
            return null;
        } else {
            existingLocation.setNaviDate(LocalDateTime.now());
            existingLocation.setNaviUser(currentUser);
            existingLocation.setSlug(locationDetails.getSlug());
            existingLocation.setName(locationDetails.getName());
            System.out.println(locationDetails.getEvents());
            return locationRepository.save(existingLocation);
        }
    }

    public boolean deleteLocation(Long id) {
        log.info("Удаление локации");
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
