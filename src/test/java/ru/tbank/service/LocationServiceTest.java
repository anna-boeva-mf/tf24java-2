package ru.tbank.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.tbank.entities.Location;
import ru.tbank.exception.EntityNotFoundException;
import ru.tbank.repository.LocationRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class LocationServiceTest {

    private LocationRepository locationRepository = new LocationRepository();
    private LocationService locationService = new LocationService(locationRepository);
    private Location location1 = new Location("yar", "Ярославль");
    private Location location2 = new Location("msk", "Москва");

    @Test
    void getLocationById() {
        locationService.createLocation(location1);
        Location returnLocation = locationService.getLocationById(1);
        assertThat(returnLocation.getId()).isEqualTo(location1.getId());
    }

    @Test
    void getAllLocations() {
        locationService.createLocation(location1);
        locationService.createLocation(location2);
        Collection<Location> locations = locationService.getAllLocations();
        Assertions.assertEquals(2, locations.size());
    }

    @Test
    void updateLocation() {
        locationService.createLocation(location2);
        locationService.updateLocation(2, location1);
        Location returnLocation = locationService.getLocationById(2);
        Assertions.assertAll(
                () -> Assertions.assertEquals("Ярославль", returnLocation.getName(), "Check Name from response"),
                () -> Assertions.assertEquals("yar", returnLocation.getSlug(), "Check Slug from response"));
    }


    @Test
    void deleteLocation() {
        locationService.createLocation(location1);
        locationService.deleteLocation(1);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            locationService.getLocationById(1);
        });
    }
}