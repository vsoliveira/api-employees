package com.company.employees.application;

import com.company.employees.domain.EmployeePageQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ListEmployeesRequest Tests")
class ListEmployeesRequestTest {

    @Test
    @DisplayName("should apply default pagination and sorting values")
    void testDefaults() {
        ListEmployeesRequest request = new ListEmployeesRequest();

        EmployeePageQuery query = request.toQuery();

        assertEquals(0, query.getPage());
        assertEquals(20, query.getSize());
        assertEquals(com.company.employees.domain.EmployeeSortField.NAME, query.getSortBy());
        assertEquals(com.company.employees.domain.SortDirection.ASC, query.getDirection());
    }

    @Test
    @DisplayName("should reject invalid pagination and sorting")
    void testValidation() {
        assertThrows(IllegalArgumentException.class, () -> new ListEmployeesRequest(-1, 20, "name", "asc").toQuery());
        assertThrows(IllegalArgumentException.class, () -> new ListEmployeesRequest(0, 0, "name", "asc").toQuery());
        assertThrows(IllegalArgumentException.class, () -> new ListEmployeesRequest(0, 101, "name", "asc").toQuery());
        assertThrows(IllegalArgumentException.class, () -> new ListEmployeesRequest(0, 20, "salary", "asc").toQuery());
        assertThrows(IllegalArgumentException.class, () -> new ListEmployeesRequest(0, 20, "name", "up").toQuery());
    }
}