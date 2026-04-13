package com.company.employees.domain;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Employee Aggregate Root.
 * Encapsulates all business logic for employee creation and validation.
 */
public class Employee {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final int MAX_DEPARTMENT_LENGTH = 50;

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
        return new Employee(UUID.randomUUID(), name.trim(), email.trim().toLowerCase(), department.trim());
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
            throw new InvalidEmployeeException("Employee name cannot be empty");
        }
        if (name.trim().length() < MIN_NAME_LENGTH || name.trim().length() > MAX_NAME_LENGTH) {
            throw new InvalidEmployeeException(
                    "Employee name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters"
            );
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmployeeException("Employee email cannot be empty");
        }
        if (email.trim().length() > MAX_EMAIL_LENGTH || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new InvalidEmployeeException("Employee email format is invalid");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new InvalidEmployeeException("Employee department cannot be empty");
        }
        if (department.trim().length() > MAX_DEPARTMENT_LENGTH) {
            throw new InvalidEmployeeException(
                    "Employee department name must not exceed " + MAX_DEPARTMENT_LENGTH + " characters"
            );
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
