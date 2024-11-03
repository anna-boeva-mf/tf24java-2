package ru.tbank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.entities.Location;
import ru.tbank.exception.EntityNotFoundException;
import ru.tbank.service.LocationService;


import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({LocationController.class})
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetLocations_FineResult() throws Exception {

        Location location1 = new Location("main", "MAIN LOCATION");
        Location location2 = new Location("std", "STADIUM");
        Location location3 = new Location("arn", "ARENA");
        List<Location> t = List.of(location1, location2, location3);
        when(locationService.getAllLocations()).thenReturn(t);

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(location1.getId()))
                .andExpect(jsonPath("$[1].slug").value(location2.getSlug()))
                .andExpect(jsonPath("$[2].name").value(location3.getName()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(t)))
                .andDo(print());
    }

    @Test
    public void testGetLocation_FineResult() throws Exception {

        Location location1 = new Location(1,"main", "MAIN LOCATION");
        when(locationService.getLocationById(1)).thenReturn(location1);

        mockMvc.perform(get("/api/v1/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(location1.getId()))
                .andExpect(jsonPath("$.slug").value(location1.getSlug()))
                .andExpect(jsonPath("$.name").value(location1.getName()))
                .andDo(print());
    }

    @Test
    public void testAddLocation_FineResult() throws Exception {
        Location location1 = new Location("main", "MAIN LOCATION");
        when(locationService.createLocation(location1)).thenReturn(1);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"main\", \"name\": \"MAIN LOCATION\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testDeleteLocation_FineResult() throws Exception {
        Location location1 = new Location(1,"main", "MAIN LOCATION");
        locationService.createLocation(location1);

        mockMvc.perform(delete("/api/v1/locations/1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testDeleteLocation_WithNoEntity() throws Exception {
        Location location1 = new Location(1,"main", "MAIN LOCATION");
        locationService.createLocation(location1);

        mockMvc.perform(delete("/api/v1/locations/8888888"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testUpdateLocation_FineResult() throws Exception {
        Location location1 = new Location("main", "MAIN LOCATION");
        locationService.createLocation(location1);

        mockMvc.perform(put("/api/v1/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"main\", \"name\": \"MAIN LOCATION\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetLocations_BadResult_WrongPath() throws Exception {
        List<Location> t = null;
        when(locationService.getAllLocations()).thenReturn(t);

        mockMvc.perform(get("/api/v1/locationsSSSSSS"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetLocation_BadResult_DoesntExist() throws Exception {

        when(locationService.getLocationById(111)).thenThrow(new EntityNotFoundException("Сущность с ID=111 не найдена"));

        mockMvc.perform(get("/api/v1/locations/111"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}
