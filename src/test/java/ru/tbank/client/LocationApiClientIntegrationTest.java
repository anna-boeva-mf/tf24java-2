package ru.tbank.client;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ContentTypes;
import com.github.tomakehurst.wiremock.common.Json;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpServerErrorException;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import ru.tbank.entities.Location;

import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class LocationApiClientIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private LocationApiClient locationApiClient;

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:3.6.0");

    @BeforeAll
    public static void setUp() {
        WireMock.configureFor(wireMockContainer.getHost(), wireMockContainer.getFirstMappedPort());
    }

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("locations.url", wireMockContainer::getBaseUrl);
    }

    @Test
    void testGetAllLocations() {
        Location location1 = new Location("main", "MAIN LOCATION");
        List<Location> locations = List.of(location1);
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", ContentTypes.APPLICATION_JSON)
                        .withBody(Json.write(locations))
                ));


        Location[] responseLocations = locationApiClient.initializeData();

        Assertions.assertAll(
                () -> assertEquals(1, responseLocations.length, "Check count of locations from response"),
                () -> assertEquals("main", responseLocations[0].getSlug(), "Check location's slug from response"),
                () -> assertEquals("MAIN LOCATION", responseLocations[0].getName(), "Check location's name from response"));


    }

    @Test
    void GetAllLocations_BadServiceCall() {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(500)
                ));
        try {
            locationApiClient.initializeData();
        } catch (HttpServerErrorException e) {
            assertEquals(500, e.getStatusCode().value(), "Expected status code 500");
        }
    }
}
