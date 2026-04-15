package com.company.hr.employees.infrastructure.rest;

import com.company.hr.employees.application.CreateEmployeeRequest;
import com.company.hr.employees.application.CreateEmployeeUseCase;
import com.company.hr.employees.application.DeleteEmployeeRequest;
import com.company.hr.employees.application.DeleteEmployeeUseCase;
import com.company.hr.employees.application.ListEmployeesRequest;
import com.company.hr.employees.application.ListEmployeesUseCase;
import com.company.hr.employees.domain.EmployeeAlreadyExistsException;
import com.company.hr.employees.domain.EmployeeNotFoundException;
import com.company.hr.employees.domain.InvalidEmployeeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Employee controller unit tests")
class EmployeeControllerUnitTest {

    @Mock
    private CreateEmployeeUseCase createEmployeeUseCase;

    @Mock
    private ListEmployeesUseCase listEmployeesUseCase;

    @Mock
    private DeleteEmployeeUseCase deleteEmployeeUseCase;

    @InjectMocks
    private EmployeeController controller;

    @Test
    @DisplayName("Create employee returns conflict when email already exists")
    void createEmployeeReturnsConflictForDuplicateEmail() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Jane Doe", "jane@example.com", "HR");
        when(createEmployeeUseCase.execute(request))
                .thenThrow(new EmployeeAlreadyExistsException(request.getEmail()));

        ResponseEntity<?> response = controller.createEmployee(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        EmployeeController.ErrorResponse errorResponse = assertInstanceOf(
                EmployeeController.ErrorResponse.class,
                response.getBody()
        );
        assertEquals("Employee with email 'jane@example.com' already exists", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Create employee returns bad request when the domain rejects the request")
    void createEmployeeReturnsBadRequestForDomainValidationFailure() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Jane Doe", "jane@example.com", "HR");
        when(createEmployeeUseCase.execute(request))
                .thenThrow(new InvalidEmployeeException("Employee name is required"));

        ResponseEntity<?> response = controller.createEmployee(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        EmployeeController.ErrorResponse errorResponse = assertInstanceOf(
                EmployeeController.ErrorResponse.class,
                response.getBody()
        );
        assertEquals("Employee name is required", errorResponse.getMessage());
    }

    @Test
    @DisplayName("List employees returns bad request when the request is invalid")
    void listEmployeesReturnsBadRequestForInvalidRequest() {
        ListEmployeesRequest request = new ListEmployeesRequest();
        when(listEmployeesUseCase.execute(request)).thenThrow(new IllegalArgumentException("Invalid sort field"));

        ResponseEntity<?> response = controller.listEmployees(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        EmployeeController.ErrorResponse errorResponse = assertInstanceOf(
                EmployeeController.ErrorResponse.class,
                response.getBody()
        );
        assertEquals("Invalid sort field", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Delete employee returns not found when the use case cannot delete the employee")
    void deleteEmployeeReturnsNotFoundWhenEmployeeDoesNotExist() {
        UUID employeeId = UUID.randomUUID();
        doThrow(new EmployeeNotFoundException("Employee not found"))
                .when(deleteEmployeeUseCase)
                                .execute(any(DeleteEmployeeRequest.class));

        ResponseEntity<?> response = controller.deleteEmployee(employeeId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Type mismatch handler falls back to generic validation message for non-UUID parameters")
        @SuppressWarnings("null")
    void handleMethodArgumentTypeMismatchReturnsGenericMessageForNonUuidParameters() throws NoSuchMethodException {
        Method method = EmployeeController.class.getDeclaredMethod("deleteEmployee", UUID.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                List.of("not-a-uuid"),
                String.class,
                "sortBy",
                parameter,
                new IllegalArgumentException("bad request")
        );

        ResponseEntity<EmployeeController.ErrorResponse> response = controller.handleMethodArgumentTypeMismatch(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        EmployeeController.ErrorResponse errorResponse = assertInstanceOf(
                EmployeeController.ErrorResponse.class,
                response.getBody()
        );
        assertEquals("Request validation failed", errorResponse.getMessage());
    }
}