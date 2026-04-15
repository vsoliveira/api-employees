package com.company.hr.employees.application;

import com.company.hr.employees.domain.Employee;
import com.company.hr.employees.domain.EmployeeNotFoundException;
import com.company.hr.employees.domain.EmployeeRepository;
import com.company.hr.employees.support.InMemoryEmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DeleteEmployeeUseCase Tests")
class DeleteEmployeeUseCaseTest {
    @Test
    @DisplayName("should delete employee successfully")
    void testDeleteEmployeeSuccess() {
        // Given
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        DeleteEmployeeUseCase useCase = new DeleteEmployeeUseCase(repository);

        Employee employee = Employee.create("John Doe", "john@example.com", "Engineering");
        repository.save(employee);
        DeleteEmployeeRequest request = new DeleteEmployeeRequest(employee.getId());

        // When/Then
        assertDoesNotThrow(() -> useCase.execute(request));
    }

    @Test
    @DisplayName("should throw exception when employee not found")
    void testDeleteNonExistentEmployee() {
        // Given
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        DeleteEmployeeUseCase useCase = new DeleteEmployeeUseCase(repository);
        UUID nonExistentId = UUID.randomUUID();
        DeleteEmployeeRequest request = new DeleteEmployeeRequest(nonExistentId);

        // Expect
        assertThrows(EmployeeNotFoundException.class, () -> useCase.execute(request));
    }
}

