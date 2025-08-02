package za.co.securemonkey.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HealthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthCheck_WhenApplicationIsRunning_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Database connection is healthy"));
    }

    @Test
    void healthCheck_EndpointIsPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    void healthCheck_ResponseContentTypeIsText() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }
}
