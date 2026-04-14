package com.company.employees.application;

import com.company.employees.domain.Employee;
import com.company.employees.domain.EmployeePageQuery;
import com.company.employees.domain.EmployeeRepository;
import com.company.employees.domain.PageResult;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Use Case: List Employees
 * Retrieves all employees from the system.
 */
@Service
public class ListEmployeesUseCase {
    private final EmployeeRepository employeeRepository;

    public ListEmployeesUseCase(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public PagedResponse<EmployeeResponse> execute(ListEmployeesRequest request) {
        EmployeePageQuery query = request.toQuery();

        PageResult<Employee> employees = employeeRepository.findAll(query);
        return new PagedResponse<>(
                employees.getContent()
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                employees.getPage(),
                employees.getSize(),
                employees.getTotalElements(),
                employees.getTotalPages(),
                request.getValidatedSortBy(),
                request.getValidatedDirection()
        );
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment()
        );
    }
}
