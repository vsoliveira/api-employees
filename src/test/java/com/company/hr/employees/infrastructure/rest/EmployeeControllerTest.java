package com.company.hr.employees.infrastructure.rest;

import com.company.hr.employees.application.CreateEmployeeRequest;
import com.company.hr.employees.infrastructure.persistence.EmployeeEntity;
import com.company.hr.employees.infrastructure.persistence.EmployeeJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
@DisplayName("Employee REST API Integration Tests")
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

        @Autowired
        private EmployeeJpaRepository employeeJpaRepository;

    private static final String API_KEY = "default-api-key";
    private static final String API_KEY_HEADER = "X-API-Key";

        @BeforeEach
        void setUp() {
                employeeJpaRepository.deleteAll();
        }

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
        mockMvc.perform(get("/v1/employees")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // With valid API key → NOT 401 (can be 200, 500, etc - auth passed)
        mockMvc.perform(get("/v1/employees")
                .header(API_KEY_HEADER, API_KEY)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(isNotUnauthorized());

        // With invalid API key → 401 Unauthorized
        mockMvc.perform(get("/v1/employees")
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
        mockMvc.perform(post("/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized());

        // With invalid API key → 401
        mockMvc.perform(post("/v1/employees")
                .header(API_KEY_HEADER, "wrong-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized());

        // With valid API key → NOT 401 (passes auth layer)
        mockMvc.perform(post("/v1/employees")
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
        mockMvc.perform(delete("/v1/employees/" + employeeId))
                .andExpect(status().isUnauthorized());

        // With invalid API key → 401
        mockMvc.perform(delete("/v1/employees/" + employeeId)
                .header(API_KEY_HEADER, "wrong-key"))
                .andExpect(status().isUnauthorized());

        // With valid API key → NOT 401 (passes auth layer)
        mockMvc.perform(delete("/v1/employees/" + employeeId)
                .header(API_KEY_HEADER, API_KEY))
                .andExpect(isNotUnauthorized());
    }

    @Test
    @DisplayName("Agent-facing endpoints bypass API key authentication")
    void testAgentFacingEndpointsBypassApiKey() throws Exception {
        assertEndpointBypassesApiKey(get("/swagger-ui.html"));
        assertEndpointBypassesApiKey(get("/v1/api-docs"));
        assertEndpointBypassesApiKey(get("/actuator/health"));
        assertEndpointBypassesApiKey(get("/actuator/metrics"));
        assertEndpointBypassesApiKey(get("/actuator/prometheus"));
    }

    private void assertEndpointBypassesApiKey(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(isNotUnauthorized());

        mockMvc.perform(requestBuilder.header(API_KEY_HEADER, "wrong-key"))
                .andExpect(isNotUnauthorized());
    }

    @Test
    @DisplayName("GET /v1/employees returns a paginated payload")
    void testListEmployeesPagination() throws Exception {
        employeeJpaRepository.save(new EmployeeEntity(
                java.util.UUID.randomUUID(),
                "Alice Doe",
                "alice@example.com",
                "Engineering"
        ));
        employeeJpaRepository.save(new EmployeeEntity(
                java.util.UUID.randomUUID(),
                "Bruno Doe",
                "bruno@example.com",
                "HR"
        ));
        employeeJpaRepository.save(new EmployeeEntity(
                java.util.UUID.randomUUID(),
                "Carla Doe",
                "carla@example.com",
                "Sales"
        ));

        mockMvc.perform(get("/v1/employees")
                .header(API_KEY_HEADER, API_KEY)
                .param("page", "0")
                .param("size", "2")
                .param("sortBy", "name")
                .param("direction", "asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Alice Doe"))
                .andExpect(jsonPath("$.content[1].name").value("Bruno Doe"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.sortBy").value("name"))
                .andExpect(jsonPath("$.direction").value("asc"));
    }

    @Test
    @DisplayName("GET /v1/employees sorts by requested field and direction")
    void testListEmployeesSorting() throws Exception {
        employeeJpaRepository.save(new EmployeeEntity(
                java.util.UUID.randomUUID(),
                "Alice Doe",
                "alice@example.com",
                "Engineering"
        ));
        employeeJpaRepository.save(new EmployeeEntity(
                java.util.UUID.randomUUID(),
                "Bruno Doe",
                "bruno@example.com",
                "HR"
        ));
        employeeJpaRepository.save(new EmployeeEntity(
                java.util.UUID.randomUUID(),
                "Carla Doe",
                "carla@example.com",
                "Sales"
        ));

        mockMvc.perform(get("/v1/employees")
                .header(API_KEY_HEADER, API_KEY)
                .param("sortBy", "department")
                .param("direction", "desc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].department").value("Sales"))
                .andExpect(jsonPath("$.content[1].department").value("HR"))
                .andExpect(jsonPath("$.content[2].department").value("Engineering"))
                .andExpect(jsonPath("$.sortBy").value("department"))
                .andExpect(jsonPath("$.direction").value("desc"));
    }

        @Test
        @DisplayName("OpenAPI documents paginated employee listing and sorting parameters")
        void testOpenApiDocumentsEmployeePageResponse() throws Exception {
                mockMvc.perform(get("/v1/api-docs").accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().string(containsString("EmployeePageResponse")))
                                .andExpect(content().string(containsString("\"sortBy\"")))
                                .andExpect(content().string(containsString("\"direction\"")))
                                .andExpect(content().string(containsString("#/components/schemas/EmployeePageResponse")))
                                .andExpect(content().string(containsString("\"maximum\":100")));
        }
}
