package com.company.employees.application;

import com.company.employees.domain.Employee;
import com.company.employees.domain.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ListEmployeesUseCase Tests")
class ListEmployeesUseCaseTest {

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
    @DisplayName("should list all employees")
    void testListEmployeesSuccess() {
        // Given
        EmployeeRepository repository = new FakeEmployeeRepository();
        ListEmployeesUseCase useCase = new ListEmployeesUseCase(repository);
        
        Employee emp1 = Employee.create("John Doe", "john@example.com", "Engineering");
        Employee emp2 = Employee.create("Jane Doe", "jane@example.com", "HR");
        repository.save(emp1);
        repository.save(emp2);

        // When
        List<EmployeeResponse> responses = useCase.execute();

        // Then
        assertEquals(2, responses.size());
        assertTrue(responses.stream().anyMatch(r -> r.getName().equals("John Doe")));
        assertTrue(responses.stream().anyMatch(r -> r.getName().equals("Jane Doe")));
    }

    @Test
    @DisplayName("should return empty list when no employees exist")
    void testListEmployeesEmpty() {
        // Given
        EmployeeRepository repository = new FakeEmployeeRepository();
        ListEmployeesUseCase useCase = new ListEmployeesUseCase(repository);

        // When
        List<EmployeeResponse> responses = useCase.execute();

        // Then
        assertEquals(0, responses.size());
    }
}

