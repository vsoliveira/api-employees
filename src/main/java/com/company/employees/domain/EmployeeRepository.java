package com.company.employees.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Port: EmployeeRepository - defines how the domain interacts with persistence.
 * Implementation is in the infrastructure layer.
 */
public interface EmployeeRepository {
    /**
     * Save a new employee.
     *
     * @param employee the employee to save
     * @return the saved employee
     * @throws EmployeeAlreadyExistsException if email already exists
     */
    Employee save(Employee employee);

    /**
     * Find an employee by ID.
     *
     * @param id the employee ID
     * @return the employee if found
     */
    Optional<Employee> findById(UUID id);

    /**
     * Find an employee by email.
     *
     * @param email the employee email
     * @return the employee if found
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Get a page of employees.
     *
        * @param query paginated and sorted employee query
     * @return paginated employees
     */
        PageResult<Employee> findAll(EmployeePageQuery query);

    /**
     * Delete an employee by ID.
     *
     * @param id the employee ID
     * @return true if deleted, false if not found
     */
    boolean deleteById(UUID id);
}
