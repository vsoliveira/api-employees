package com.company.hr.employees.application;

import com.company.hr.employees.domain.EmployeePageQuery;
import com.company.hr.employees.domain.EmployeeSortField;
import com.company.hr.shared.domain.SortDirection;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for employee listing with centralized defaults and validation.
 */
public class ListEmployeesRequest {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_BY = "name";
    public static final String DEFAULT_DIRECTION = "asc";

    @Parameter(description = "Zero-based page index")
    @Schema(defaultValue = "0", minimum = "0")
    @Min(value = 0, message = "Page index must be greater than or equal to zero")
    private int page = DEFAULT_PAGE;

    @Parameter(description = "Number of employees per page")
    @Schema(defaultValue = "20", minimum = "1", maximum = "100")
    @Min(value = 1, message = "Page size must be greater than zero")
    @Max(value = 100, message = "Page size must be less than or equal to 100")
    private int size = DEFAULT_SIZE;

    @Parameter(description = "Field used to sort employees")
    @Schema(defaultValue = "name", allowableValues = {"id", "name", "email", "department"})
    @NotBlank(message = "Sort field must not be blank")
    private String sortBy = DEFAULT_SORT_BY;

    @Parameter(description = "Sort direction")
    @Schema(defaultValue = "asc", allowableValues = {"asc", "desc"})
    @NotBlank(message = "Sort direction must not be blank")
    private String direction = DEFAULT_DIRECTION;

    public ListEmployeesRequest() {
    }

    public ListEmployeesRequest(int page, int size, String sortBy, String direction) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.direction = direction;
    }

    public EmployeePageQuery toQuery() {
        validatePagination();
        EmployeeSortField sortField = EmployeeSortField.from(sortBy);
        SortDirection sortDirection = SortDirection.from(direction);
        return new EmployeePageQuery(page, size, sortField, sortDirection);
    }

    public String getValidatedSortBy() {
        return EmployeeSortField.from(sortBy).getParameterValue();
    }

    public String getValidatedDirection() {
        return SortDirection.from(direction).getParameterValue();
    }

    private void validatePagination() {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must be greater than or equal to zero");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be less than or equal to " + MAX_PAGE_SIZE);
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}