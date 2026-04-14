package com.company.employees.application;

import com.company.employees.domain.Employee;
import com.company.employees.domain.EmployeeRepository;
import com.company.employees.support.InMemoryEmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ListEmployeesUseCase Tests")
class ListEmployeesUseCaseTest {
    @Test
    @DisplayName("should return the requested employee page")
    void testListEmployeesSuccess() {
        // Given
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        ListEmployeesUseCase useCase = new ListEmployeesUseCase(repository);
        
        Employee emp1 = Employee.create("Alice Doe", "alice@example.com", "Engineering");
        Employee emp2 = Employee.create("Bruno Doe", "bruno@example.com", "HR");
        Employee emp3 = Employee.create("Carla Doe", "carla@example.com", "Sales");
        repository.save(emp1);
        repository.save(emp2);
        repository.save(emp3);

        // When
        PagedResponse<EmployeeResponse> response = useCase.execute(
            new ListEmployeesRequest(0, 2, "name", "asc")
        );

        // Then
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPage());
        assertEquals(2, response.getSize());
        assertEquals(3, response.getTotalElements());
        assertEquals(2, response.getTotalPages());
        assertEquals("name", response.getSortBy());
        assertEquals("asc", response.getDirection());
        assertEquals("Alice Doe", response.getContent().get(0).getName());
        assertEquals("Bruno Doe", response.getContent().get(1).getName());
    }

    @Test
    @DisplayName("should sort employees using requested field and direction")
    void testListEmployeesSortsByRequestedField() {
        // Given
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        ListEmployeesUseCase useCase = new ListEmployeesUseCase(repository);

        repository.save(Employee.create("Alice Doe", "alice@example.com", "Engineering"));
        repository.save(Employee.create("Bruno Doe", "bruno@example.com", "HR"));
        repository.save(Employee.create("Carla Doe", "carla@example.com", "Sales"));

        // When
        PagedResponse<EmployeeResponse> response = useCase.execute(
            new ListEmployeesRequest(0, 3, "department", "desc")
        );

        // Then
        assertEquals("department", response.getSortBy());
        assertEquals("desc", response.getDirection());
        assertEquals("Sales", response.getContent().get(0).getDepartment());
        assertEquals("HR", response.getContent().get(1).getDepartment());
        assertEquals("Engineering", response.getContent().get(2).getDepartment());
    }

    @Test
    @DisplayName("should return empty page when no employees exist")
    void testListEmployeesEmpty() {
        // Given
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        ListEmployeesUseCase useCase = new ListEmployeesUseCase(repository);

        // When
        PagedResponse<EmployeeResponse> response = useCase.execute(new ListEmployeesRequest());

        // Then
        assertEquals(0, response.getContent().size());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
    }

    @Test
    @DisplayName("should reject invalid pagination parameters")
    void testListEmployeesWithInvalidPagination() {
        // Given
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        ListEmployeesUseCase useCase = new ListEmployeesUseCase(repository);
        ListEmployeesRequest invalidPage = new ListEmployeesRequest(-1, 20, "name", "asc");
        ListEmployeesRequest invalidSize = new ListEmployeesRequest(0, 0, "name", "asc");
        ListEmployeesRequest oversized = new ListEmployeesRequest(0, 101, "name", "asc");
        ListEmployeesRequest invalidSort = new ListEmployeesRequest(0, 20, "salary", "asc");
        ListEmployeesRequest invalidDirection = new ListEmployeesRequest(0, 20, "name", "up");

        // Then
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(invalidPage));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(invalidSize));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(oversized));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(invalidSort));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(invalidDirection));
    }
}

