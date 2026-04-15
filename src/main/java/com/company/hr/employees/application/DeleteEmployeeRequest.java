package com.company.hr.employees.application;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Command: DeleteEmployeeRequest
 */
public class DeleteEmployeeRequest {
    @NotNull(message = "Employee id must not be null")
    private final UUID employeeId;

    public DeleteEmployeeRequest(UUID employeeId) {
        this.employeeId = Objects.requireNonNull(employeeId, "Employee id must not be null");
    }

    public UUID getEmployeeId() {
        return employeeId;
    }
}