package ru.tbank.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void testLogin_OK() throws Exception {
        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"User1\",\"password\": \"Mypassword\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        MvcResult afterResult = mockMvc.perform(post("/api/v1/auth/login?rememberMe=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"User1\",\"password\": \"Mypassword\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonAfterResult = new String(afterResult.getResponse().getContentAsByteArray());
        JsonObject jsonObject = JsonParser.parseString(jsonAfterResult).getAsJsonObject();
        String accessToken = jsonObject.get("accessToken").getAsString();
        String refreshToken = jsonObject.get("refreshToken").getAsString();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(accessToken),
                () -> Assertions.assertNotNull(refreshToken)
        );
    }

    @Test
    void testLogout_OK() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"User1\",\"password\": \"Mypassword\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testResetPassword_OK() throws Exception {
        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"User890\",\"password\": \"Mypassword\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        MvcResult afterResult = mockMvc.perform(post("/api/v1/auth/login?rememberMe=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"User890\",\"password\": \"Mypassword\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonAfterResult = new String(afterResult.getResponse().getContentAsByteArray());
        JsonObject jsonObject = JsonParser.parseString(jsonAfterResult).getAsJsonObject();
        String accessToken = jsonObject.get("accessToken").getAsString();

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{            \"token\": \"" + accessToken + "\"," +
                                "                \"newPassword\": \"password2\"," +
                                "                \"confirmPassword\": \"password2\"," +
                                "                \"verificationCode\": \"0000\"" +
                                "        }"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testResetPassword_Wrong2faCode() throws Exception {
        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"User432\",\"password\": \"Mypassword\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        MvcResult afterResult = mockMvc.perform(post("/api/v1/auth/login?rememberMe=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"User432\",\"password\": \"Mypassword\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonAfterResult = new String(afterResult.getResponse().getContentAsByteArray());
        JsonObject jsonObject = JsonParser.parseString(jsonAfterResult).getAsJsonObject();
        String accessToken = jsonObject.get("accessToken").getAsString();

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{            \"token\": \"" + accessToken + "\"," +
                                "                \"newPassword\": \"password2\"," +
                                "                \"confirmPassword\": \"password2\"," +
                                "                \"verificationCode\": \"1234\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid verification code"))
                .andDo(print());
    }

    @Test
    void testGetApi_NotAuthorized() throws Exception {
        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void testGetApi_OK_Authorized() throws Exception {
        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"User3\",\"password\": \"Mypassword\"}"))
                .andExpect(status().isCreated())
                .andDo(print());

        MvcResult afterResult = mockMvc.perform(post("/api/v1/auth/login?rememberMe=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"User3\",\"password\": \"Mypassword\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonAfterResult = new String(afterResult.getResponse().getContentAsByteArray());
        JsonObject jsonObject = JsonParser.parseString(jsonAfterResult).getAsJsonObject();
        String accessToken = jsonObject.get("accessToken").getAsString();

        mockMvc.perform(get("/api/v1/events")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
}