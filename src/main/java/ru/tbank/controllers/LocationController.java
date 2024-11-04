package ru.tbank.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.EventDTO;
import ru.tbank.dto.LocationDTO;
import ru.tbank.entities.Location;
import ru.tbank.logging.LogExecutionTime;
import ru.tbank.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
@RestController
@RequestMapping({"/api/v1/locations"})
@LogExecutionTime
@AllArgsConstructor
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<LocationDTO> locationsDTO = locationService.getAllLocations();
        return new ResponseEntity<>(locationsDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id) {
        LocationDTO locationDTO = locationService.getLocationById(id);
        if (locationDTO != null) {
            return new ResponseEntity<>(locationDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<LocationDTO> getLocationWithEvents(@PathVariable Long id) {
        LocationDTO locationDTO = locationService.getLocationWithEvents(id);
        if (locationDTO != null) {
            return new ResponseEntity<>(locationDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody Location location) {
        LocationDTO createdLocationDTO = locationService.createLocation(location);
        return new ResponseEntity<>(createdLocationDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable Long id, @RequestBody Location locationDetails) {
        LocationDTO updatedLocationDTO = locationService.updateLocation(id, locationDetails);
        if (updatedLocationDTO != null) {
            return new ResponseEntity<>(updatedLocationDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        boolean isDeleted = locationService.deleteLocation(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
