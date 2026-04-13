package com.company.employees.infrastructure.rest;

import com.company.employees.application.CreateEmployeeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Employee REST API Integration Tests")
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_KEY = "default-api-key";
    private static final String API_KEY_HEADER = "X-API-Key";

    /**
     * Matcher for any non-401 status (any response that isn't unauthorized)
     */
    private static ResultMatcher isNotUnauthorized() {
        return result -> {
            int status = result.getResponse().getStatus();
            if (status == 401) {
                throw new AssertionError("Expected status other than 401, but was 401 (Unauthorized)");
            }
        };
    }

    @Test
    @DisplayName("GET /v1/employees - API key authentication")
    void testListEmployeesAuth() throws Exception {
        // Without API key → 401 Unauthorized
        mockMvc.perform(get("/api/v1/employees")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // With valid API key → NOT 401 (can be 200, 500, etc - auth passed)
        mockMvc.perform(get("/api/v1/employees")
                .header(API_KEY_HEADER, API_KEY)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(isNotUnauthorized());

        // With invalid API key → 401 Unauthorized
        mockMvc.perform(get("/api/v1/employees")
                .header(API_KEY_HEADER, "wrong-key")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /v1/employees - API key authentication")
    void testCreateEmployeeAuth() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "John Doe", "john@example.com", "Engineering"
        );
        String jsonRequest = objectMapper.writeValueAsString(request);

        // Without API key → 401
        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized());

        // With invalid API key → 401
        mockMvc.perform(post("/api/v1/employees")
                .header(API_KEY_HEADER, "wrong-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized());

        // With valid API key → NOT 401 (passes auth layer)
        mockMvc.perform(post("/api/v1/employees")
                .header(API_KEY_HEADER, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(isNotUnauthorized());
    }

    @Test
    @DisplayName("DELETE /v1/employees/{id} - API key authentication")
    void testDeleteEmployeeAuth() throws Exception {
        String employeeId = "00000000-0000-0000-0000-000000000000";

        // Without API key → 401
        mockMvc.perform(delete("/api/v1/employees/" + employeeId))
                .andExpect(status().isUnauthorized());

        // With invalid API key → 401
        mockMvc.perform(delete("/api/v1/employees/" + employeeId)
                .header(API_KEY_HEADER, "wrong-key"))
                .andExpect(status().isUnauthorized());

        // With valid API key → NOT 401 (passes auth layer)
        mockMvc.perform(delete("/api/v1/employees/" + employeeId)
                .header(API_KEY_HEADER, API_KEY))
                .andExpect(isNotUnauthorized());
    }
}
