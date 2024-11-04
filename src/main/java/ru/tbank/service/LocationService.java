package ru.tbank.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.tbank.db_repository.LocationRepository;
import ru.tbank.dto.LocationDTO;
import ru.tbank.entities.Location;
import ru.tbank.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LocationService {

    @Value("${spring.datasource.username}")
    private String currentUser;

    @Autowired
    private LocationRepository locationRepository;


    public List<LocationDTO> getAllLocations() {
        log.info("Получение всех локаций");
        List<Location> locations = locationRepository.findAll();
        if (locations.isEmpty()) {
            log.warn("Список событий пуст");
            return null;
        } else {
            List<LocationDTO> locationsDTO = locations.stream()
                    .map(location -> new LocationDTO(location, false))
                    .collect(Collectors.toList());
            return locationsDTO;
        }
    }

    public LocationDTO getLocationById(Long id) {
        log.info("Получение локации по ID");
        Optional<Location> location = locationRepository.findById(id);
        if (location.isPresent()) {
            return new LocationDTO(location.get(), false);
        } else {
            log.error("Локация не найдена");
            return null;
        }
    }

    public LocationDTO getLocationWithEvents(Long id) {
        log.info("Получение локации с событиями по ID");
        Location location = locationRepository.findByIdWithEvents(id);
        if (location != null) {
            return new LocationDTO(location, true);
        } else {
            log.error("Локация не найдена");
            return null;
        }
    }

    public LocationDTO createLocation(Location location) {
        log.info("Создание локации");
        try {
            if (locationRepository.existsBySlug(location.getSlug())) {
                log.warn("Локация уже существует");
                return new LocationDTO(locationRepository.findBySlug(location.getSlug()), false);
            } else {
                location.setNaviDate(LocalDateTime.now());
                location.setNaviUser(currentUser);
                return new LocationDTO(locationRepository.save(location), false);
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка добавления новой локации");
            throw new BadRequestException(e.getMessage());
        }
    }

    public LocationDTO updateLocation(Long id, Location locationDetails) {
        log.info("Обновление локации");
        try {
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
                return new LocationDTO(locationRepository.save(existingLocation), false);
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка обновления локации");
            throw new BadRequestException(e.getMessage());
        }
    }

    public boolean deleteLocation(Long id) {
        log.info("Удаление локации");
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
            return true;
        } else {
            log.error("Локация не найдена");
            return false;
        }
    }
}
