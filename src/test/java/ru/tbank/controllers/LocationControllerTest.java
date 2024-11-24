package ru.tbank.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import ru.tbank.db_repository.EventRepository;

import org.springframework.http.MediaType;
import ru.tbank.db_repository.LocationRepository;
import ru.tbank.entities.Event;
import ru.tbank.entities.Location;
import ru.tbank.service.LocationService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LocationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationController locationController;

    @Autowired
    private EventRepository eventRepository;

    @Mock
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    public static PostgreSQLContainer<?> pgDB = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("kudago_test")
            .withUsername("pguser_test")
            .withPassword("pgpwd_test");

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", pgDB::getJdbcUrl);
        registry.add("spring.datasource.username", pgDB::getUsername);
        registry.add("spring.datasource.password", pgDB::getPassword);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ekb", "kzn", "msk", "nnv", "spb"})
    public void testGetAllLocations_LoadedSlugsInInitializer(String expectedSlug) throws Exception {
        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].slug").value(hasItem(expectedSlug)))
                .andDo(print());
    }

    @Test
    public void testGetLocation() throws Exception {
        MvcResult location = mockMvc.perform(get("/api/v1/locations/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonLocation = new String(location.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        Location resultLocation = objectMapper.readValue(jsonLocation, new TypeReference<Location>() {
        });

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, resultLocation.getLocationId(), "ИД локации"),
                () -> Assertions.assertEquals(locationRepository.findById(1L).get().getSlug(), resultLocation.getSlug(), "Код локации"),
                () -> Assertions.assertEquals(locationRepository.findById(1L).get().getName(), resultLocation.getName(), "Название"),
                () -> Assertions.assertTrue(locationRepository.existsBySlug(resultLocation.getSlug()), "Код локации есть в репозитории"));
    }

    @Test
    public void testGetLocation_NotExistent() throws Exception {
        mockMvc.perform(get("/api/v1/locations/1111111111"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetLocation_WithEvents() throws Exception {
        MvcResult location = mockMvc.perform(get("/api/v1/locations/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonLocation = new String(location.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        Location resultLocation = objectMapper.readValue(jsonLocation, new TypeReference<Location>() {
        });

        Long locationId = resultLocation.getLocationId();
        String locationSlug = resultLocation.getSlug();
        String locationName = resultLocation.getName();

        String locationContent = "\"location\":{\"locationId\":" + locationId + ",\"name\":\"" + locationName + "\",\"slug\":\"" + locationSlug + "\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Name1\",\"slug\": \"name1\"," + locationContent + ",\"startDate\":\"2024-01-01T00:00:00\",\"endDate\":\"2025-01-01T00:00:00\",\"site_url\": \"https://kudago.com/msk/event/name1/\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Name2\",\"slug\": \"name2\"," + locationContent + ",\"startDate\":\"2024-01-01T00:00:00\",\"endDate\":\"2025-01-01T00:00:00\",\"site_url\": \"https://kudago.com/msk/event/name2/\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        MvcResult locationWithEvents = mockMvc.perform(get("/api/v1/locations/events/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonLocationWithEvents = new String(locationWithEvents.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        Location resultLocationWithEvents = objectMapper.readValue(jsonLocationWithEvents, new TypeReference<Location>() {
        });

        List<Event> events = resultLocationWithEvents.getEvents();

        Assertions.assertAll(
                () -> Assertions.assertTrue(events.size() >= 2, "Два события точно есть"),
                () -> Assertions.assertTrue(events.stream().anyMatch(event -> event.getSlug().equals("name1"))),
                () -> Assertions.assertTrue(events.stream().anyMatch(event -> event.getSlug().equals("name2")))
        );
    }

    @Test
    public void testAddLocation() throws Exception {
        MvcResult newLocation = mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Нью-Йорк\",\"slug\":\"new-york\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonLocation = new String(newLocation.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        Location locationResult = objectMapper.readValue(jsonLocation, new TypeReference<Location>() {
        });

        Assertions.assertAll(
                () -> Assertions.assertEquals("new-york", locationResult.getSlug(), "Код локации"),
                () -> Assertions.assertEquals("Нью-Йорк", locationResult.getName(), "Имя локации")
        );
    }

    @Test
    public void testAddLocation_WithNoSlug() throws Exception {
        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Нью-Йорк\"}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateLocation_ChangeName() throws Exception {
        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Владимир\",\"slug\":\"vlad\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
        Long locationId = locationRepository.findBySlug("vlad").getLocationId();

        mockMvc.perform(put("/api/v1/locations/" + locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Владивосток\",\"slug\":\"vlad\"}"))
                .andExpect(status().isOk())
                .andDo(print());

        Assertions.assertEquals("Владивосток", locationRepository.findById(locationId).get().getName(), "Имя обновилось");
    }

    @Test
    public void testUpdateLocation_NonExistent() throws Exception {
        Long locationId = 123456789L;

        mockMvc.perform(put("/api/v1/locations/" + locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Владивосток\",\"slug\":\"vlad\"}"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUpdateLocation_ByExistingSlug() throws Exception {
        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Владимир\",\"slug\":\"vlad\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
        Long locationId1 = locationRepository.findBySlug("vlad").getLocationId();

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Владикавказ\",\"slug\":\"vladkvz\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
        Long locationId2 = locationRepository.findBySlug("vladkvz").getLocationId();

        // второму объекту хотим присвоить код (и имя) как у первого
        mockMvc.perform(put("/api/v1/locations/" + locationId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Владимир\",\"slug\":\"vlad\"}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testDeleteLocation_AndEventsWereDeleted() throws Exception {
        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ереван\",\"slug\":\"yrvn\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
        Long locationId = locationRepository.findBySlug("yrvn").getLocationId();
        String locationSlug = locationRepository.findBySlug("yrvn").getSlug();
        String locationName = locationRepository.findBySlug("yrvn").getName();

        String locationContent = "\"location\":{\"locationId\":" + locationId + ",\"name\":\"" + locationName + "\",\"slug\":\"" + locationSlug + "\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Name1\",\"slug\": \"name1\"," + locationContent + ",\"startDate\":\"2024-01-01T00:00:00\",\"endDate\":\"2025-01-01T00:00:00\",\"site_url\": \"https://kudago.com/msk/event/name1/\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Name2\",\"slug\": \"name2\"," + locationContent + ",\"startDate\":\"2024-01-01T00:00:00\",\"endDate\":\"2025-01-01T00:00:00\",\"site_url\": \"https://kudago.com/msk/event/name2/\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        mockMvc.perform(delete("/api/v1/locations/" + locationId))
                .andExpect(status().isNoContent())
                .andDo(print());

        // во второй раз уже "не найдено"
        mockMvc.perform(delete("/api/v1/locations/" + locationId))
                .andExpect(status().isNotFound())
                .andDo(print());

        Assertions.assertAll(
                () -> Assertions.assertFalse(eventRepository.existsBySlug("name1"), "Нет первого события"),
                () -> Assertions.assertFalse(eventRepository.existsBySlug("name2"), "Нет второго события"),
                () -> Assertions.assertFalse(locationRepository.existsBySlug(locationSlug), "Нет локации")
        );
    }
}