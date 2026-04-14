package com.company.employees.infrastructure.rest;

import com.company.employees.application.CreateEmployeeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("null")
@DisplayName("Employee REST Validation Tests")
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerValidationTest {

    private static final String API_KEY = "default-api-key";
    private static final String API_KEY_HEADER = "X-API-Key";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /v1/employees rejects invalid create payload")
    void testCreateEmployeeRejectsInvalidRequestBody() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(new CreateEmployeeRequest(
                "A", "john@example.com", "Engineering"
        ));

        mockMvc.perform(post("/v1/employees")
                .header(API_KEY_HEADER, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Employee name must be between 2 and 100 characters"));
    }

    @Test
    @DisplayName("GET /v1/employees rejects invalid pagination")
    void testListEmployeesRejectsInvalidPagination() throws Exception {
        mockMvc.perform(get("/v1/employees")
                .header(API_KEY_HEADER, API_KEY)
                .param("page", "-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page index must be greater than or equal to zero"));
    }

    @Test
    @DisplayName("GET /v1/employees rejects oversized pagination")
    void testListEmployeesRejectsOversizedPagination() throws Exception {
        mockMvc.perform(get("/v1/employees")
                .header(API_KEY_HEADER, API_KEY)
                .param("size", "101")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page size must be less than or equal to 100"));
    }

    @Test
    @DisplayName("GET /v1/employees rejects invalid sorting")
    void testListEmployeesRejectsInvalidSorting() throws Exception {
        mockMvc.perform(get("/v1/employees")
                .header(API_KEY_HEADER, API_KEY)
                .param("sortBy", "salary")
                .param("direction", "asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported sort field: salary. Supported values are: id, name, email, department"));
    }

    @Test
    @DisplayName("DELETE /v1/employees/{id} rejects invalid uuid")
    void testDeleteEmployeeRejectsInvalidUuid() throws Exception {
        mockMvc.perform(delete("/v1/employees/not-a-uuid")
                .header(API_KEY_HEADER, API_KEY)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Employee id must be a valid UUID"));
    }
}