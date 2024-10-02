package ru.tbank.controllers;

import java.util.Collection;

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
import ru.tbank.entities.Location;
import ru.tbank.logging.LogExecutionTime;
import ru.tbank.service.LocationService;

@Slf4j
@RestController
@RequestMapping({"/api/v1/locations"})
@LogExecutionTime
@AllArgsConstructor
public class LocationController {
    @Autowired
    private final LocationService locationService;

    @GetMapping
    public Collection<Location> getAllLocations() {
        return this.locationService.getAllLocations();
    }

    @GetMapping({"/{id}"})
    public Location getLocationById(@PathVariable int id) {
        return this.locationService.getLocationById(id);
    }

    @PostMapping
    public void createLocation(@RequestBody Location location) {
        this.locationService.createLocation(location);
    }

    @PutMapping({"/{id}"})
    public void updateLocation(@PathVariable int id, @RequestBody Location location) {
        this.locationService.updateLocation(id, location);
    }

    @DeleteMapping({"/{id}"})
    public void deleteLocation(@PathVariable int id) {
        this.locationService.deleteLocation(id);
    }
}
