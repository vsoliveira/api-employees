package com.company.hr.employees.application;

import com.company.hr.employees.domain.EmployeeNotFoundException;
import com.company.hr.employees.domain.EmployeeRepository;
import org.springframework.stereotype.Service;

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

    public void execute(DeleteEmployeeRequest request) {
        boolean deleted = employeeRepository.deleteById(request.getEmployeeId());

        if (!deleted) {
            throw new EmployeeNotFoundException("Employee with id " + request.getEmployeeId() + " not found");
        }
    }
}
