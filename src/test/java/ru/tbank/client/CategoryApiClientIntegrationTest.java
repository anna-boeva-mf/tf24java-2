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
import ru.tbank.entities.Category;

import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CategoryApiClientIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private CategoryApiClient categoryApiClient;

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:3.6.0");

    @BeforeAll
    public static void setUp() {
        WireMock.configureFor(wireMockContainer.getHost(), wireMockContainer.getFirstMappedPort());
    }

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("categories.url", wireMockContainer::getBaseUrl);
    }

    @Test
    void testGetAllCategories() {
        Category category1 = new Category("MAIN CATEGORY","main");
        List<Category> categories = List.of(category1);
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", ContentTypes.APPLICATION_JSON)
                        .withBody(Json.write(categories))
                ));


        Category[] responseCategories = categoryApiClient.initializeData();

        Assertions.assertAll(
                () -> assertEquals(1, responseCategories.length, "Check count of categories from response"),
                () -> assertEquals("main", responseCategories[0].getSlug(), "Check category's slug from response"),
                () -> assertEquals("MAIN CATEGORY", responseCategories[0].getName(), "Check category's name from response"));


    }

    @Test
    void GetAllCategories_BadServiceCall() {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(500)
                ));
        try {
            categoryApiClient.initializeData();
        } catch (HttpServerErrorException e) {
            assertEquals(500, e.getStatusCode().value(), "Expected status code 500");
        }
    }
}
