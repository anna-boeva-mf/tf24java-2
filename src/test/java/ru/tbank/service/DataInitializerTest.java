package ru.tbank.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.client.CategoryApiClient;
import ru.tbank.client.LocationApiClient;
import ru.tbank.entities.Category;
import ru.tbank.entities.Location;
import ru.tbank.repository.CategoryRepository;
import ru.tbank.repository.LocationRepository;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest({DataInitializerTest.class})
class DataInitializerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataInitializer dataInitializer;
    @Mock
    private CategoryApiClient categoryApiClient;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private LocationApiClient locationApiClient;
    @Mock
    private LocationRepository locationRepository;

    @Test
    void runTest() {
        dataInitializer = new DataInitializer(categoryApiClient, categoryRepository, locationApiClient, locationRepository);
        Location location1 = new Location("main", "MAIN LOCATION");
        Location[] t1 = {location1};
        Category category1 = new Category("main", "MAIN CATEGORY");
        Category[] t2 = {category1};

        when(locationApiClient.initializeData()).thenReturn(t1);
        when(categoryApiClient.initializeData()).thenReturn(t2);

        dataInitializer.run(null);

        verify(locationApiClient).initializeData();
        verify(categoryApiClient).initializeData();
    }
}