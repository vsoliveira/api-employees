package com.company.employees.application;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Concrete paginated employee response used by the API contract and OpenAPI schema.
 */
@Schema(name = "EmployeePageResponse", description = "Paginated employee response")
public class EmployeePageResponse {
    @ArraySchema(schema = @Schema(implementation = EmployeeResponse.class))
    private final List<EmployeeResponse> content;

    @Schema(description = "Zero-based page index", example = "0")
    private final int page;

    @Schema(description = "Requested page size", example = "20")
    private final int size;

    @Schema(description = "Total number of employees matching the query", example = "42")
    private final long totalElements;

    @Schema(description = "Total number of result pages", example = "3")
    private final int totalPages;

    @Schema(description = "Applied sort field", example = "name", allowableValues = {"id", "name", "email", "department"})
    private final String sortBy;

    @Schema(description = "Applied sort direction", example = "asc", allowableValues = {"asc", "desc"})
    private final String direction;

    public EmployeePageResponse(
            List<EmployeeResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            String sortBy,
            String direction
    ) {
        this.content = List.copyOf(content);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.sortBy = sortBy;
        this.direction = direction;
    }

    public static EmployeePageResponse from(PagedResponse<EmployeeResponse> response) {
        return new EmployeePageResponse(
                response.getContent(),
                response.getPage(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages(),
                response.getSortBy(),
                response.getDirection()
        );
    }

    public List<EmployeeResponse> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getDirection() {
        return direction;
    }
}