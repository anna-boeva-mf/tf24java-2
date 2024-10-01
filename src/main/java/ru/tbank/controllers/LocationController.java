package ru.tbank.controllers;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.tbank.services.LocationService;

@RestController
@RequestMapping({"/api/v1/locations"})
public class LocationController {
    private static final Logger log = LoggerFactory.getLogger(LocationController.class);
    @Autowired
    private final LocationService locationService;

    @LogExecutionTime
    @GetMapping
    public Collection<Location> getAllLocations() {
        return this.locationService.getAllLocations();
    }

    @LogExecutionTime
    @GetMapping({"/{id}"})
    public Location getLocationById(@PathVariable int id) {
        return this.locationService.getLocationById(id);
    }

    @LogExecutionTime
    @PostMapping
    public void createLocation(@RequestBody Location location) {
        this.locationService.createLocation(location);
    }

    @LogExecutionTime
    @PutMapping({"/{id}"})
    public void updateLocation(@PathVariable int id, @RequestBody Location location) {
        this.locationService.updateLocation(id, location);
    }

    @LogExecutionTime
    @DeleteMapping({"/{id}"})
    public void deleteLocation(@PathVariable int id) {
        this.locationService.deleteLocation(id);
    }

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }
}
