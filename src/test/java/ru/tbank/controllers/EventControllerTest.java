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
import ru.tbank.service.CategoryService;
import ru.tbank.service.EventService;

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
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

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
    @ValueSource(strings = {"name1", "name2", "name3"})
    public void testGetAllEvents_AfterAddingThem(String expectedSlug) throws Exception {
        Location mskLocation = locationRepository.findBySlug("msk");
        String locationContent = "\"location\":{\"locationId\":" + mskLocation.getLocationId() + ",\"name\":\"Москва\",\"slug\":\"msk\"}";

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

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
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Name3\",\"slug\": \"name3\"," + locationContent + ",\"startDate\":\"2024-01-01T00:00:00\",\"endDate\":\"2025-01-01T00:00:00\",\"site_url\": \"https://kudago.com/msk/event/name3/\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].slug").value(hasItem(expectedSlug)))
                .andDo(print());
    }

    @Test
    public void testAddEvent_AndItExistsInRepository() throws Exception {
        Location mskLocation = locationRepository.findBySlug("msk");
        String locationContent = "\"location\":{\"locationId\":" + mskLocation.getLocationId() + ",\"name\":\"Москва\",\"slug\":\"msk\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"иммерсивная инсталляция «Пропавшие в кинохронике»\",\"slug\": \"vyistavka-propavshie-v-kinohronike\"," + locationContent + ",\"startDate\":\"2024-10-09T18:00:00\",\"endDate\":\"2024-11-11T21:15:00\",\"site_url\": \"https://kudago.com/msk/event/777\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        Assertions.assertTrue(eventRepository.existsBySlug("vyistavka-propavshie-v-kinohronike"));
    }

    @Test
    public void testAddEvent_TwiceButNoError() throws Exception {
        Location mskLocation = locationRepository.findBySlug("msk");
        String locationContent = "\"location\":{\"locationId\":" + mskLocation.getLocationId() + ",\"name\":\"Москва\",\"slug\":\"msk\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Name1\",\"slug\": \"name1\"," + locationContent + ",\"startDate\":\"2024-01-01T00:00:00\",\"endDate\":\"2025-01-01T00:00:00\",\"site_url\": \"https://kudago.com/msk/event/name1/\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Name1\",\"slug\": \"name1\"," + locationContent + ",\"startDate\":\"2024-01-01T00:00:00\",\"endDate\":\"2025-01-01T00:00:00\",\"site_url\": \"https://kudago.com/msk/event/name1/\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void testAddEvent_WithoutLocation() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Name1\",\"slug\": \"name1\"," + "\"startDate\":\"2024-01-01T00:00:00\",\"endDate\":\"2025-01-01T00:00:00\",\"site_url\": \"https://kudago.com/msk/event/name1/\"}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testAddEvent_WithoutSlug() throws Exception {

        Location mskLocation = locationRepository.findBySlug("msk");
        String locationContent = "\"location\":{\"locationId\":" + mskLocation.getLocationId() + ",\"name\":\"Москва\",\"slug\":\"msk\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Name1\"," + locationContent + ",\"startDate\":\"2024-01-01T00:00:00\",\"endDate\":\"2025-01-01T00:00:00\",\"site_url\": \"https://kudago.com/msk/event/name1/\"}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testAddEvent_UnknownLocation() throws Exception {
        String NYLocationContent = "\"location\":{\"locationId\":111,\"name\":\"Нью-Йорк\",\"slug\":\"new-york\"}";
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"The Phantom of the Opera Musical\",\"slug\": \"the-phantom-of-the-opera\"," + NYLocationContent + ",\"site_url\": \"https://kudago.com/new-york/event/the-phantom-of-the-opera/\"}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateEvent_ButSlugAlredyExistsInRepository() throws Exception {
        Location mskLocation = locationRepository.findBySlug("msk");
        String locationContent = "\"location\":{\"locationId\":" + mskLocation.getLocationId() + ",\"name\":\"Москва\",\"slug\":\"msk\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"два билета на спектакли в Московском драматическом театре под руководством Армена Джигарханяна со скидкой до 50%\",\"slug\": \"skidkabum-dzigartheater\"," + locationContent + ",\"startDate\":\"2024-11-09T18:00:00\",\"endDate\":\"2024-12-11T21:00:00\",\"site_url\": \"https://kudago.com/msk/event/skidkabum-dzigartheater/\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        Long firstAddedEventId = eventRepository.findBySlug("skidkabum-dzigartheater").getEventId();

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"экскурсия на двухэтажном автобусе\",\"slug\": \"dvuhetazhnie-avtobusi\"," + locationContent + ",\"startDate\":\"2024-01-09T18:00:00\",\"endDate\":\"2024-01-11T21:00:00\",\"site_url\": \"https://kudago.com/msk/event/dvuhetazhnie-avtobusi/\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        Long secondAddedEventId = eventRepository.findBySlug("dvuhetazhnie-avtobusi").getEventId();

        // апдейтим второе событие, чтобы было как первое, но slug уже занят первым событием
        mockMvc.perform(put("/api/v1/events/" + secondAddedEventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"два билета на спектакли в Московском драматическом театре под руководством Армена Джигарханяна со скидкой до 50%\",\"slug\": \"skidkabum-dzigartheater\"," + locationContent + ",\"startDate\":\"2024-11-09T18:00:00\",\"endDate\":\"2024-12-11T21:00:00\",\"site_url\": \"https://kudago.com/msk/event/skidkabum-dzigartheater/\"}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateEvent_UpdateName() throws Exception {
        Location mskLocation = locationRepository.findBySlug("msk");
        String locationContent = "\"location\":{\"locationId\":" + mskLocation.getLocationId() + ",\"name\":\"Москва\",\"slug\":\"msk\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"EVENT1\",\"slug\": \"event1\"," + locationContent + ",\"startDate\":\"2024-11-09T18:00:00\",\"endDate\":\"2024-12-11T21:00:00\",\"site_url\": \"https://kudago.com/msk/event/event1\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        Long eventId = eventRepository.findBySlug("event1").getEventId();

        mockMvc.perform(put("/api/v1/events/" + eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"EVENT11111111\",\"slug\": \"event1\"," + locationContent + ",\"startDate\":\"2024-11-09T18:00:00\",\"endDate\":\"2024-12-11T21:00:00\",\"site_url\": \"https://kudago.com/msk/event/event1\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testDeleteEvent() throws Exception {
        Location mskLocation = locationRepository.findBySlug("msk");
        String locationContent = "\"location\":{\"locationId\":" + mskLocation.getLocationId() + ",\"name\":\"Москва\",\"slug\":\"msk\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"EVENT1\",\"slug\": \"event1\"," + locationContent + ",\"startDate\":\"2024-11-09T18:00:00\",\"endDate\":\"2024-12-11T21:00:00\",\"site_url\": \"https://kudago.com/msk/event/event1\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        Long eventId = eventRepository.findBySlug("event1").getEventId();

        mockMvc.perform(delete("/api/v1/events/" + eventId))
                .andExpect(status().isNoContent())
                .andDo(print());

        Event eventAfterDeleting = eventRepository.findBySlug("event1");

        Assertions.assertTrue(eventAfterDeleting == null);
    }

    @Test
    public void testUpdateEvent_NotExistentEvent() throws Exception {
        Location mskLocation = locationRepository.findBySlug("msk");
        String locationContent = "\"location\":{\"locationId\":" + mskLocation.getLocationId() + ",\"name\":\"Москва\",\"slug\":\"msk\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"EVENT1\",\"slug\": \"event1\"," + locationContent + ",\"startDate\":\"2024-11-09T18:00:00\",\"endDate\":\"2024-12-11T21:00:00\",\"site_url\": \"https://kudago.com/msk/event/event1\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        Long eventId = eventRepository.findBySlug("event1").getEventId();

        mockMvc.perform(delete("/api/v1/events/" + eventId))
                .andExpect(status().isNoContent())
                .andDo(print());

        mockMvc.perform(put("/api/v1/events/" + eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"EVENT1\",\"slug\": \"event1\"," + locationContent + ",\"startDate\":\"2024-11-09T18:00:00\",\"endDate\":\"2024-12-11T21:00:00\",\"site_url\": \"https://kudago.com/msk/event/event1\"}"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testDeleteEvent_NotExistentEvent() throws Exception {
        Location mskLocation = locationRepository.findBySlug("msk");
        String locationContent = "\"location\":{\"locationId\":" + mskLocation.getLocationId() + ",\"name\":\"Москва\",\"slug\":\"msk\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"EVENT1\",\"slug\": \"event1\"," + locationContent + ",\"startDate\":\"2024-11-09T18:00:00\",\"endDate\":\"2024-12-11T21:00:00\",\"site_url\": \"https://kudago.com/msk/event/event1\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        Long eventId = eventRepository.findBySlug("event1").getEventId();

        mockMvc.perform(delete("/api/v1/events/" + eventId))
                .andExpect(status().isNoContent())
                .andDo(print());

        // во второй раз уже "не найдено"
        mockMvc.perform(delete("/api/v1/events/" + eventId))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testSearchEvent() throws Exception {
        //почистить репозиторий
        eventRepository.findAll().forEach(v -> eventRepository.delete(v));

        Location mskLocation = locationRepository.findBySlug("msk");
        String locationContent = "\"location\":{\"locationId\":" + mskLocation.getLocationId() + ",\"name\":\"Москва\",\"slug\":\"msk\"}";

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Призраки и вампиры\",\"slug\": \"ghosts-and-vampires\"," + locationContent +
                                ",\"startDate\":\"2022-01-01T00:00:00\",\"endDate\":\"2022-12-31T23:59:59\",\"site_url\": \"https://kudago.com/msk/event/ghosts-and-vampires\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Оборотни и вампиры среди нас\",\"slug\": \"werewolves-and-vampires-among-us\"," + locationContent +
                                ",\"startDate\":\"2023-11-01T00:00:00\",\"endDate\":\"2023-11-28T00:00:00\",\"site_url\": \"https://kudago.com/msk/event/werewolves-and-vampires-among-us\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Вебинар по психологии\",\"slug\": \"webinar-psychology\"," + locationContent +
                                ",\"startDate\":\"2023-11-01T19:00:00\",\"endDate\":\"2023-11-01T21:00:00\",\"site_url\": \"https://kudago.com/msk/event/webinar-psychology\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        MvcResult dateFilterResult = mockMvc.perform(get("/api/v1/events/search?fromDate=2023-11-01T00:00:00&toDate=2023-12-01T00:00:00"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonDateFilterResult = new String(dateFilterResult.getResponse().getContentAsByteArray());
        int dateFilterResultCount = objectMapper.readValue(jsonDateFilterResult, new TypeReference<List<Event>>() {
        }).size();
        Event firstEventD = objectMapper.readValue(jsonDateFilterResult, new TypeReference<List<Event>>() {
        }).get(0);
        Event secondEventD = objectMapper.readValue(jsonDateFilterResult, new TypeReference<List<Event>>() {
        }).get(1);

        MvcResult nameAndLocationFilterResult = mockMvc.perform(get("/api/v1/events/search?name=вампир&locationId=" + mskLocation.getLocationId()))
                .andExpect(status().isOk())
                .andReturn();

        String jsonNameAndLocationFilterResult = new String(nameAndLocationFilterResult.getResponse().getContentAsByteArray());
        int nameAndLocationFilterResultCount = objectMapper.readValue(jsonNameAndLocationFilterResult, new TypeReference<List<Event>>() {
        }).size();
        Event firstEventNL = objectMapper.readValue(jsonNameAndLocationFilterResult, new TypeReference<List<Event>>() {
        }).get(0);
        Event secondEventNL = objectMapper.readValue(jsonNameAndLocationFilterResult, new TypeReference<List<Event>>() {
        }).get(1);


        Assertions.assertAll(
                () -> Assertions.assertEquals(2, dateFilterResultCount, "Количество результатов поиска по дате"),
                () -> Assertions.assertEquals(2, nameAndLocationFilterResultCount, "Количество результатов поиска по имени и городу"),
                () -> Assertions.assertTrue((firstEventD.getSlug().equals("werewolves-and-vampires-among-us") && secondEventD.getSlug().equals("webinar-psychology"))
                                || (secondEventD.getSlug().equals("werewolves-and-vampires-among-us") && firstEventD.getSlug().equals("webinar-psychology")),
                        "Проверяем код событий в результате поиска по дате"),
                () -> Assertions.assertTrue((firstEventNL.getSlug().equals("werewolves-and-vampires-among-us") && secondEventNL.getSlug().equals("ghosts-and-vampires"))
                                || (secondEventNL.getSlug().equals("werewolves-and-vampires-among-us") && firstEventNL.getSlug().equals("ghosts-and-vampires")),
                        "Проверяем код событий в результате поиска по имени и городу"));
    }
}
