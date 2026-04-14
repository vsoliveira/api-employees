package com.company.employees.application;

import com.company.employees.domain.EmployeeAlreadyExistsException;
import com.company.employees.domain.EmployeeRepository;
import com.company.employees.support.InMemoryEmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateEmployeeUseCase Tests")
class CreateEmployeeUseCaseTest {
    @Test
    @DisplayName("should create employee successfully")
    void testCreateEmployeeSuccess() {
        // Given
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        CreateEmployeeUseCase useCase = new CreateEmployeeUseCase(repository);
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "John Doe", "john@example.com", "Engineering"
        );

        // When
        EmployeeResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("Engineering", response.getDepartment());
    }

    @Test
    @DisplayName("should throw exception when email already exists")
    void testCreateEmployeeWithDuplicateEmail() {
        // Given
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        CreateEmployeeUseCase useCase = new CreateEmployeeUseCase(repository);
        CreateEmployeeRequest request1 = new CreateEmployeeRequest(
                "John Doe", "john@example.com", "Engineering"
        );

        // Save first employee
        useCase.execute(request1);

        // Try to save duplicate email
        CreateEmployeeRequest request2 = new CreateEmployeeRequest(
                "Different Name", "john@example.com", "Sales"
        );

        // Expect
        assertThrows(EmployeeAlreadyExistsException.class, () -> useCase.execute(request2));
    }
}
