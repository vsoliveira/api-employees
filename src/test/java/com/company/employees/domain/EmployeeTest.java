package com.company.employees.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Employee Domain Tests")
class EmployeeTest {

    @Test
    @DisplayName("should create employee with valid data")
    void testCreateEmployeeSuccess() {
        // When
        Employee employee = Employee.create("John Doe", "john@example.com", "Engineering");

        // Then
        assertNotNull(employee.getId());
        assertEquals("John Doe", employee.getName());
        assertEquals("john@example.com", employee.getEmail());
        assertEquals("Engineering", employee.getDepartment());
    }

    @Test
    @DisplayName("should trim and lowercase email when creating employee")
    void testEmailNormalization() {
        // When
        Employee employee = Employee.create("Jane Doe", "  JANE@EXAMPLE.COM  ", "HR");

        // Then
        assertEquals("jane@example.com", employee.getEmail());
    }

    @Test
    @DisplayName("should throw exception when name is null")
    void testCreateEmployeeWithNullName() {
        // Expect
        assertThrows(InvalidEmployeeException.class, () ->
                Employee.create(null, "john@example.com", "Engineering")
        );
    }

    @Test
    @DisplayName("should throw exception when name is empty")
    void testCreateEmployeeWithEmptyName() {
        // Expect
        assertThrows(InvalidEmployeeException.class, () ->
                Employee.create("   ", "john@example.com", "Engineering")
        );
    }

    @Test
    @DisplayName("should throw exception when name is too short")
    void testCreateEmployeeWithShortName() {
        // Expect
        assertThrows(InvalidEmployeeException.class, () ->
                Employee.create("A", "john@example.com", "Engineering")
        );
    }

    @Test
    @DisplayName("should throw exception when name is too long")
    void testCreateEmployeeWithLongName() {
        // Given
        String longName = "A".repeat(101);

        // Expect
        assertThrows(InvalidEmployeeException.class, () ->
                Employee.create(longName, "john@example.com", "Engineering")
        );
    }

    @Test
    @DisplayName("should throw exception when email is null")
    void testCreateEmployeeWithNullEmail() {
        // Expect
        assertThrows(InvalidEmployeeException.class, () ->
                Employee.create("John Doe", null, "Engineering")
        );
    }

    @Test
    @DisplayName("should throw exception when email is empty")
    void testCreateEmployeeWithEmptyEmail() {
        // Expect
        assertThrows(InvalidEmployeeException.class, () ->
                Employee.create("John Doe", "   ", "Engineering")
        );
    }

    @Test
    @DisplayName("should throw exception when email format is invalid")
    void testCreateEmployeeWithInvalidEmailFormat() {
        // Expect
        assertThrows(InvalidEmployeeException.class, () ->
                Employee.create("John Doe", "not-an-email", "Engineering")
        );
    }

    @Test
    @DisplayName("should throw exception when department is null")
    void testCreateEmployeeWithNullDepartment() {
        // Expect
        assertThrows(InvalidEmployeeException.class, () ->
                Employee.create("John Doe", "john@example.com", null)
        );
    }

    @Test
    @DisplayName("should throw exception when department is empty")
    void testCreateEmployeeWithEmptyDepartment() {
        // Expect
        assertThrows(InvalidEmployeeException.class, () ->
                Employee.create("John Doe", "john@example.com", "   ")
        );
    }

    @Test
    @DisplayName("should throw exception when department is too long")
    void testCreateEmployeeWithLongDepartment() {
        // Given
        String longDepartment = "A".repeat(51);

        // Expect
        assertThrows(InvalidEmployeeException.class, () ->
                Employee.create("John Doe", "john@example.com", longDepartment)
        );
    }

    @Test
    @DisplayName("should be equal based on ID")
    void testEmployeeEquality() {
        // Given
        Employee emp1 = Employee.create("John Doe", "john@example.com", "Engineering");
        Employee emp2 = Employee.fromPersistence(emp1.getId(), "Different Name", "different@example.com", "Sales");

        // Then
        assertEquals(emp1, emp2);
    }
}
