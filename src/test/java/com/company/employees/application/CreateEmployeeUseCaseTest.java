package com.company.employees.application;

import com.company.employees.domain.Employee;
import com.company.employees.domain.EmployeeAlreadyExistsException;
import com.company.employees.domain.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateEmployeeUseCase Tests")
class CreateEmployeeUseCaseTest {

    private static class FakeEmployeeRepository implements EmployeeRepository {
        private java.util.Map<String, Employee> employees = new java.util.HashMap<>();
        private java.util.Map<UUID, Employee> employeesByID = new java.util.HashMap<>();

        @Override
        public Employee save(Employee employee) {
            if (employees.containsKey(employee.getEmail())) {
                throw new EmployeeAlreadyExistsException(employee.getEmail());
            }
            employees.put(employee.getEmail(), employee);
            employeesByID.put(employee.getId(), employee);
            return employee;
        }

        @Override
        public Optional<Employee> findById(UUID id) {
            return Optional.ofNullable(employeesByID.get(id));
        }

        @Override
        public Optional<Employee> findByEmail(String email) {
            return Optional.ofNullable(employees.get(email));
        }

        @Override
        public java.util.List<Employee> findAll() {
            return new java.util.ArrayList<>(employees.values());
        }

        @Override
        public boolean deleteById(UUID id) {
            Employee employee = employeesByID.remove(id);
            if (employee != null) {
                employees.remove(employee.getEmail());
                return true;
            }
            return false;
        }
    }

    @Test
    @DisplayName("should create employee successfully")
    void testCreateEmployeeSuccess() {
        // Given
        EmployeeRepository repository = new FakeEmployeeRepository();
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
        EmployeeRepository repository = new FakeEmployeeRepository();
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
