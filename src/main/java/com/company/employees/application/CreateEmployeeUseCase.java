package com.company.employees.application;

import com.company.employees.domain.Employee;
import com.company.employees.domain.EmployeeAlreadyExistsException;
import com.company.employees.domain.EmployeeRepository;
import org.springframework.stereotype.Service;

/**
 * Use Case: Create Employee
 * Receives a request to create a new employee.
 */
@Service
public class CreateEmployeeUseCase {
    private final EmployeeRepository employeeRepository;

    public CreateEmployeeUseCase(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeResponse execute(CreateEmployeeRequest request) {
        // Check if email already exists
        if (employeeRepository.findByEmail(request.getEmail().trim().toLowerCase()).isPresent()) {
            throw new EmployeeAlreadyExistsException(request.getEmail());
        }

        // Create and save the employee
        Employee employee = Employee.create(
                request.getName(),
                request.getEmail(),
                request.getDepartment()
        );

        Employee saved = employeeRepository.save(employee);

        // Return DTO
        return toResponse(saved);
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
