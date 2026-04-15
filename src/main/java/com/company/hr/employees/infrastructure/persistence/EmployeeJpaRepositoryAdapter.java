package com.company.hr.employees.infrastructure.persistence;

import com.company.hr.employees.domain.Employee;
import com.company.hr.employees.domain.EmployeePageQuery;
import com.company.hr.employees.domain.EmployeeRepository;
import com.company.hr.employees.domain.EmployeeSortField;
import com.company.hr.shared.domain.PageResult;
import com.company.hr.shared.domain.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter: Implements the domain EmployeeRepository port using JPA.
 * Maps between domain Employee and JPA EmployeeEntity.
 */
@Service
public class EmployeeJpaRepositoryAdapter implements EmployeeRepository {
    private final EmployeeJpaRepository jpaRepository;

    public EmployeeJpaRepositoryAdapter(EmployeeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Employee save(Employee employee) {
        EmployeeEntity entity = new EmployeeEntity(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment()
        );
        EmployeeEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        Objects.requireNonNull(id, "Employee id must not be null");
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public PageResult<Employee> findAll(EmployeePageQuery query) {
        Objects.requireNonNull(query, "Employee page query must not be null");
        Sort sort = Objects.requireNonNull(toSpringSort(query), "Spring sort must not be null");

        Page<EmployeeEntity> employeePage = jpaRepository.findAll(PageRequest.of(
                query.getPage(),
                query.getSize(),
            sort
        ));

        return new PageResult<>(
                employeePage.getContent().stream().map(this::toDomain).toList(),
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages()
        );
    }

    private Sort toSpringSort(EmployeePageQuery query) {
        Sort.Direction direction = query.getDirection() == SortDirection.DESC
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        String property = Objects.requireNonNull(toProperty(query.getSortBy()), "Sort property must not be null");
        Sort.Order primaryOrder = new Sort.Order(direction, property);
        if (query.getSortBy() == EmployeeSortField.ID) {
            return Objects.requireNonNull(Sort.by(primaryOrder), "Spring sort must not be null");
        }
        return Objects.requireNonNull(Sort.by(primaryOrder, Sort.Order.asc("id")), "Spring sort must not be null");
    }

    private String toProperty(EmployeeSortField sortField) {
        return switch (sortField) {
            case ID -> "id";
            case NAME -> "name";
            case EMAIL -> "email";
            case DEPARTMENT -> "department";
        };
    }

    @Override
    public boolean deleteById(UUID id) {
        Objects.requireNonNull(id, "Employee id must not be null");
        if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Employee toDomain(EmployeeEntity entity) {
        return Employee.fromPersistence(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getDepartment()
        );
    }
}
