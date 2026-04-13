package com.company.employees.application;

import com.company.employees.domain.EmployeeNotFoundException;
import com.company.employees.domain.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use Case: Delete Employee
 * Removes an employee from the system.
 */
@Service
public class DeleteEmployeeUseCase {
    private final EmployeeRepository employeeRepository;

    public DeleteEmployeeUseCase(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void execute(UUID employeeId) {
        boolean deleted = employeeRepository.deleteById(employeeId);

        if (!deleted) {
            throw new EmployeeNotFoundException("Employee with id " + employeeId + " not found");
        }
    }
}
