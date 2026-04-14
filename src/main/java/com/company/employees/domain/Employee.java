package com.company.employees.domain;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Employee Aggregate Root.
 * Encapsulates all business logic for employee creation and validation.
 */
public class Employee {
    private final UUID id;
    private final String name;
    private final String email;
    private final String department;

    private Employee(UUID id, String name, String email, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    /**
     * Create a new Employee. Validates all fields before creation.
     *
     * @param name the employee name
     * @param email the employee email
     * @param department the employee department
     * @return a new Employee instance
     * @throws InvalidEmployeeException if validation fails
     */
    public static Employee create(String name, String email, String department) {
        validate(name, email, department);
        return new Employee(
                UUID.randomUUID(),
                name.trim(),
                email.trim().toLowerCase(Locale.ROOT),
                department.trim()
        );
    }

    /**
     * Reconstruct an existing Employee from persistence layer.
     */
    public static Employee fromPersistence(UUID id, String name, String email, String department) {
        return new Employee(id, name, email, department);
    }

    /**
     * Validate all employee fields.
     *
     * @throws InvalidEmployeeException if any field is invalid
     */
    private static void validate(String name, String email, String department) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidEmployeeException(EmployeeValidationRules.NAME_REQUIRED_MESSAGE);
        }
        if (name.trim().length() < EmployeeValidationRules.MIN_NAME_LENGTH
                || name.trim().length() > EmployeeValidationRules.MAX_NAME_LENGTH) {
            throw new InvalidEmployeeException(EmployeeValidationRules.NAME_LENGTH_MESSAGE);
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmployeeException(EmployeeValidationRules.EMAIL_REQUIRED_MESSAGE);
        }
        if (email.trim().length() > EmployeeValidationRules.MAX_EMAIL_LENGTH
                || !EmployeeValidationRules.COMPILED_EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new InvalidEmployeeException(EmployeeValidationRules.EMAIL_FORMAT_MESSAGE);
        }
        if (department == null || department.trim().isEmpty()) {
            throw new InvalidEmployeeException(EmployeeValidationRules.DEPARTMENT_REQUIRED_MESSAGE);
        }
        if (department.trim().length() > EmployeeValidationRules.MAX_DEPARTMENT_LENGTH) {
            throw new InvalidEmployeeException(EmployeeValidationRules.DEPARTMENT_LENGTH_MESSAGE);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
