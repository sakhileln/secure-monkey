package za.co.securemonkey.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import za.co.securemonkey.dto.LoginRequest;
import za.co.securemonkey.entity.User;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_WithValidData_ReturnsCreatedUser() throws Exception {
        User newUser = new User();
        newUser.setEmail("integration@test.com");
        newUser.setPassword("password123");
        newUser.setName("Integration Test User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("integration@test.com")))
                .andExpect(jsonPath("$.name", is("Integration Test User")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void registerUser_WithDuplicateEmail_ReturnsConflict() throws Exception {
        User newUser = new User();
        newUser.setEmail("duplicate@test.com");
        newUser.setPassword("password123");
        newUser.setName("Duplicate Test User");

        // First registration should succeed
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk());

        // Second registration with same email should fail
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict());
    }

    @Test
    void loginUser_WithValidCredentials_ReturnsToken() throws Exception {
        // First register a user
        User newUser = new User();
        newUser.setEmail("login@test.com");
        newUser.setPassword("password123");
        newUser.setName("Login Test User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk());

        // Then try to login
        LoginRequest loginRequest = new LoginRequest("login@test.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.email", is("login@test.com")))
                .andExpect(jsonPath("$.type", is("Bearer")));
    }

    @Test
    void loginUser_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@test.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginUser_WithInvalidEmailFormat_ReturnsBadRequest() throws Exception {
        LoginRequest loginRequest = new LoginRequest("invalid-email-format", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WithInvalidEmailFormat_ReturnsBadRequest() throws Exception {
        User invalidUser = new User();
        invalidUser.setEmail("invalid-email-format");
        invalidUser.setPassword("password123");
        invalidUser.setName("Test User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WithMissingFields_ReturnsBadRequest() throws Exception {
        User incompleteUser = new User();
        incompleteUser.setEmail("test@example.com");
        // Missing password and name

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteUser)))
                .andExpect(status().isBadRequest());
    }
}
