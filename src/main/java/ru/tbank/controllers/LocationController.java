package ru.tbank.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.tbank.logging.LogExecutionTime;
import ru.tbank.entities.Location;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {
    private final GenericRepository<Location> locationRepository = new GenericRepository<>();


    public LocationController() {
        initializeData();
    }

    @LogExecutionTime
    private void initializeData() {
        RestTemplate restTemplate = new RestTemplate();

        String locationsUrl = "https://kudago.com/public-api/v1.4/locations/";
        Location[] locations = restTemplate.getForObject(locationsUrl, Location[].class);
        for (Location location : locations) {
            locationRepository.save(location.getSlug(), location);
        }
    }


    @LogExecutionTime
    @GetMapping
    public List<Location> getAllLocations() {
        return List.copyOf(locationRepository.findAll());
    }

    @LogExecutionTime
    @GetMapping("/{id}")
    public Location getLocationById(@PathVariable String id) {
        return locationRepository.findById(id);
    }

    @LogExecutionTime
    @PostMapping
    public void createLocation(@RequestBody Location location) {
        locationRepository.save(location.getSlug(), location);
    }

    @LogExecutionTime
    @PutMapping("/{id}")
    public void updateLocation(@PathVariable String id, @RequestBody Location location) {
        locationRepository.save(id, location);
    }

    @LogExecutionTime
    @DeleteMapping("/{id}")
    public void deleteLocation(@PathVariable String id) {
        locationRepository.delete(id);
    }
}