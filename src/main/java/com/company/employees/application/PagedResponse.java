package com.company.employees.application;

import java.util.List;

/**
 * Application DTO representing a paginated response payload.
 */
public class PagedResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final String sortBy;
    private final String direction;

    public PagedResponse(
            List<T> content,
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

    public List<T> getContent() {
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