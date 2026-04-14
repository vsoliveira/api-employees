package com.company.employees.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("DeleteEmployeeRequest Tests")
class DeleteEmployeeRequestTest {

    @Test
    @DisplayName("should keep the provided employee id")
    void testRequestStoresEmployeeId() {
        UUID employeeId = UUID.randomUUID();

        DeleteEmployeeRequest request = new DeleteEmployeeRequest(employeeId);

        assertEquals(employeeId, request.getEmployeeId());
    }

    @Test
    @DisplayName("should reject null employee id")
    void testRequestRejectsNullId() {
        assertThrows(NullPointerException.class, () -> new DeleteEmployeeRequest(null));
    }
}