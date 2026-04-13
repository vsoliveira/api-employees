package com.company.employees.application;

import com.company.employees.domain.Employee;
import com.company.employees.domain.EmployeeNotFoundException;
import com.company.employees.domain.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DeleteEmployeeUseCase Tests")
class DeleteEmployeeUseCaseTest {

    private static class FakeEmployeeRepository implements EmployeeRepository {
        private java.util.Map<String, Employee> employees = new java.util.HashMap<>();
        private java.util.Map<UUID, Employee> employeesByID = new java.util.HashMap<>();

        @Override
        public Employee save(Employee employee) {
            if (employees.containsKey(employee.getEmail())) {
                throw new com.company.employees.domain.EmployeeAlreadyExistsException(employee.getEmail());
            }
            employees.put(employee.getEmail(), employee);
            employeesByID.put(employee.getId(), employee);
            return employee;
        }

        @Override
        public java.util.Optional<Employee> findById(UUID id) {
            return java.util.Optional.ofNullable(employeesByID.get(id));
        }

        @Override
        public java.util.Optional<Employee> findByEmail(String email) {
            return java.util.Optional.ofNullable(employees.get(email));
        }

        @Override
        public List<Employee> findAll() {
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
    @DisplayName("should delete employee successfully")
    void testDeleteEmployeeSuccess() {
        // Given
        EmployeeRepository repository = new FakeEmployeeRepository();
        DeleteEmployeeUseCase useCase = new DeleteEmployeeUseCase(repository);
        
        Employee employee = Employee.create("John Doe", "john@example.com", "Engineering");
        repository.save(employee);

        // When/Then
        assertDoesNotThrow(() -> useCase.execute(employee.getId()));
    }

    @Test
    @DisplayName("should throw exception when employee not found")
    void testDeleteNonExistentEmployee() {
        // Given
        EmployeeRepository repository = new FakeEmployeeRepository();
        DeleteEmployeeUseCase useCase = new DeleteEmployeeUseCase(repository);
        UUID nonExistentId = UUID.randomUUID();

        // Expect
        assertThrows(EmployeeNotFoundException.class, () -> useCase.execute(nonExistentId));
    }
}

