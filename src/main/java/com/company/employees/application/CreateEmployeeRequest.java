package com.company.employees.application;

import java.util.Objects;

/**
 * Command: CreateEmployeeRequest
 */
public class CreateEmployeeRequest {
    private final String name;
    private final String email;
    private final String department;

    public CreateEmployeeRequest(String name, String email, String department) {
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.department = Objects.requireNonNull(department);
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
}
