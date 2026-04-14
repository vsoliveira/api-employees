package com.company.employees.application;

import com.company.employees.domain.EmployeeValidationRules;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Command: CreateEmployeeRequest
 */
public class CreateEmployeeRequest {
    @NotBlank(message = EmployeeValidationRules.NAME_REQUIRED_MESSAGE)
    @Size(
            min = EmployeeValidationRules.MIN_NAME_LENGTH,
            max = EmployeeValidationRules.MAX_NAME_LENGTH,
            message = EmployeeValidationRules.NAME_LENGTH_MESSAGE
    )
    private final String name;

    @NotBlank(message = EmployeeValidationRules.EMAIL_REQUIRED_MESSAGE)
    @Size(max = EmployeeValidationRules.MAX_EMAIL_LENGTH, message = EmployeeValidationRules.EMAIL_FORMAT_MESSAGE)
    @Pattern(regexp = EmployeeValidationRules.EMAIL_PATTERN, message = EmployeeValidationRules.EMAIL_FORMAT_MESSAGE)
    private final String email;

    @NotBlank(message = EmployeeValidationRules.DEPARTMENT_REQUIRED_MESSAGE)
    @Size(max = EmployeeValidationRules.MAX_DEPARTMENT_LENGTH, message = EmployeeValidationRules.DEPARTMENT_LENGTH_MESSAGE)
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
