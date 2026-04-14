package com.company.employees.domain;

import java.util.Objects;

/**
 * Value object describing a paginated and sorted employee listing query.
 */
public class EmployeePageQuery {
    private final int page;
    private final int size;
    private final EmployeeSortField sortBy;
    private final SortDirection direction;

    public EmployeePageQuery(int page, int size, EmployeeSortField sortBy, SortDirection direction) {
        this.page = page;
        this.size = size;
        this.sortBy = Objects.requireNonNull(sortBy, "Sort field must not be null");
        this.direction = Objects.requireNonNull(direction, "Sort direction must not be null");
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public EmployeeSortField getSortBy() {
        return sortBy;
    }

    public SortDirection getDirection() {
        return direction;
    }
}