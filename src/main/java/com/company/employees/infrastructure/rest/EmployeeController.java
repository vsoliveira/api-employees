package com.company.employees.infrastructure.rest;

import com.company.employees.application.CreateEmployeeRequest;
import com.company.employees.application.CreateEmployeeUseCase;
import com.company.employees.application.DeleteEmployeeUseCase;
import com.company.employees.application.EmployeeResponse;
import com.company.employees.application.ListEmployeesUseCase;
import com.company.employees.domain.EmployeeAlreadyExistsException;
import com.company.employees.domain.EmployeeNotFoundException;
import com.company.employees.domain.InvalidEmployeeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller: Adapter exposing the employee use cases via HTTP API.
 */
@RestController
@RequestMapping("/v1/employees")
@Tag(name = "Employees", description = "Employee management API")
public class EmployeeController {

    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final ListEmployeesUseCase listEmployeesUseCase;
    private final DeleteEmployeeUseCase deleteEmployeeUseCase;

    public EmployeeController(
            CreateEmployeeUseCase createEmployeeUseCase,
            ListEmployeesUseCase listEmployeesUseCase,
            DeleteEmployeeUseCase deleteEmployeeUseCase
    ) {
        this.createEmployeeUseCase = createEmployeeUseCase;
        this.listEmployeesUseCase = listEmployeesUseCase;
        this.deleteEmployeeUseCase = deleteEmployeeUseCase;
    }

    @Operation(summary = "Create a new employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid employee data"),
            @ApiResponse(responseCode = "409", description = "Employee with this email already exists")
    })
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeRequest request) {
        try {
            EmployeeResponse response = createEmployeeUseCase.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidEmployeeException e) {
            return ResponseEntity.badRequest().body(ErrorResponse.of(e.getMessage()));
        } catch (EmployeeAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(e.getMessage()));
        }
    }

    @Operation(summary = "List all employees")
    @ApiResponse(responseCode = "200", description = "List of employees",
            content = @Content(schema = @Schema(implementation = EmployeeResponse.class)))
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> listEmployees() {
        List<EmployeeResponse> employees = listEmployeesUseCase.execute();
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Delete an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(
            @Parameter(description = "Employee ID", required = true)
            @PathVariable UUID id
    ) {
        try {
            deleteEmployeeUseCase.execute(id);
            return ResponseEntity.noContent().build();
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Error response DTO
     */
    static class ErrorResponse {
        private final String message;

        private ErrorResponse(String message) {
            this.message = message;
        }

        public static ErrorResponse of(String message) {
            return new ErrorResponse(message);
        }

        public String getMessage() {
            return message;
        }
    }
}
