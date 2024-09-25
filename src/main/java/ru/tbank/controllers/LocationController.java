package ru.tbank.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.tbank.GenericRepository;
import ru.tbank.logging.LogExecutionTime;
import ru.tbank.entities.Location;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {
    private final GenericRepository<Location> locationRepository = new GenericRepository<>();

    private static int counter = 1;

    public int genId() {
        return counter++;
    }

    public LocationController() {
        initializeData();
    }

    @LogExecutionTime
    private void initializeData() {
        RestTemplate restTemplate = new RestTemplate();

        String locationsUrl = "https://kudago.com/public-api/v1.4/locations/";
        Location[] locations = restTemplate.getForObject(locationsUrl, Location[].class);
        assert locations != null;
        for (Location location : locations) {
            int Id = genId();
            location.setId(Id);
            locationRepository.save(Id, location);
        }
    }

    @LogExecutionTime
    @GetMapping
    public List<Location> getAllLocations() {
        return List.copyOf(locationRepository.findAll());
    }

    @LogExecutionTime
    @GetMapping("/{id}")
    public Location getLocationById(@PathVariable int id) {
        return locationRepository.findById(id);
    }

    @LogExecutionTime
    @PostMapping
    public void createLocation(@RequestBody Location location) {
        int Id = genId();
        location.setId(Id);
        locationRepository.save(Id, location);
    }

    @LogExecutionTime
    @PutMapping("/{id}")
    public void updateLocation(@PathVariable int id, @RequestBody Location location) {
        location.setId(id);
        locationRepository.save(id, location);
    }

    @LogExecutionTime
    @DeleteMapping("/{id}")
    public void deleteLocation(@PathVariable int id) {
        locationRepository.delete(id);
    }
}
