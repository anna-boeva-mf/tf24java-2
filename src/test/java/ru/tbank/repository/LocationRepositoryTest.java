package ru.tbank.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tbank.entities.Location;
import ru.tbank.exception.EntityNotFoundException;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class LocationRepositoryTest {

    private LocationRepository locationRepository;
    private Location location1;
    private Location location2;

    @BeforeEach
    public void setup() {
        locationRepository = new LocationRepository();
        location1 = new Location("yar", "Ярославль");
        location2 = new Location("msk", "Москва");
        locationRepository.save(1, location1);
        locationRepository.save(2, location2);
    }

    @Test
    void findById_Fine() {
        Location location = locationRepository.findById(1);
        Assertions.assertAll(
                () -> Assertions.assertEquals("Ярославль", location.getName(), "Check Name from response"),
                () -> Assertions.assertEquals("yar", location.getSlug(), "Check Slug from response"));
    }

    @Test
    void saveTest_Fine() {
        Location location3 = new Location("new location", "NEW");
        Integer ID = locationRepository.save(3, location3);
        assertThat(ID).isEqualTo(3);
    }

    @Test
    void delete_Fine() {
        locationRepository.delete(1);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            locationRepository.findById(1);
        });
    }

    @Test
    void delete_nonexistent_Fine() {
        locationRepository.delete(5);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            locationRepository.findById(5);
        });
    }

    @Test
    void findAll_Fine() {
        Collection<Location> locations = locationRepository.findAll();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, locations.size(), "Check count from response"),
                () -> Assertions.assertEquals(true, locations.contains(location1), "Check location1 exists in response"),
                () -> Assertions.assertEquals(true, locations.contains(location2), "Check location2 exists in  response"));
    }
}