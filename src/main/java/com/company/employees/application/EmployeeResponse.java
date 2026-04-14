package com.company.employees.application;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO: EmployeeResponse
 */
public class EmployeeResponse {
    private final UUID id;
    private final String name;
    private final String email;
    private final String department;

    public EmployeeResponse(UUID id, String name, String email, String department) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.department = Objects.requireNonNull(department);
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
}
