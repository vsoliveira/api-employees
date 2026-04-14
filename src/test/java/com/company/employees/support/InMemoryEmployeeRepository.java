package com.company.employees.support;

import com.company.employees.domain.Employee;
import com.company.employees.domain.EmployeeAlreadyExistsException;
import com.company.employees.domain.EmployeePageQuery;
import com.company.employees.domain.EmployeeRepository;
import com.company.employees.domain.EmployeeSortField;
import com.company.employees.domain.PageResult;
import com.company.employees.domain.SortDirection;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryEmployeeRepository implements EmployeeRepository {
    private final Map<String, Employee> employeesByEmail = new HashMap<>();
    private final Map<UUID, Employee> employeesById = new HashMap<>();

    @Override
    public Employee save(Employee employee) {
        if (employeesByEmail.containsKey(employee.getEmail())) {
            throw new EmployeeAlreadyExistsException(employee.getEmail());
        }
        employeesByEmail.put(employee.getEmail(), employee);
        employeesById.put(employee.getId(), employee);
        return employee;
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        return Optional.ofNullable(employeesById.get(id));
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return Optional.ofNullable(employeesByEmail.get(email));
    }

    @Override
    public PageResult<Employee> findAll(EmployeePageQuery query) {
        java.util.List<Employee> sortedEmployees = employeesByEmail.values().stream()
                .sorted(comparatorFor(query))
                .toList();
        java.util.List<Employee> content = sortedEmployees.stream()
                .skip((long) query.getPage() * query.getSize())
                .limit(query.getSize())
                .toList();
        int totalPages = query.getSize() == 0
                ? 0
                : (int) Math.ceil((double) sortedEmployees.size() / query.getSize());
        return new PageResult<>(content, query.getPage(), query.getSize(), sortedEmployees.size(), totalPages);
    }

    @Override
    public boolean deleteById(UUID id) {
        Employee employee = employeesById.remove(id);
        if (employee != null) {
            employeesByEmail.remove(employee.getEmail());
            return true;
        }
        return false;
    }

    private Comparator<Employee> comparatorFor(EmployeePageQuery query) {
        Comparator<Employee> comparator = switch (query.getSortBy()) {
            case ID -> Comparator.comparing(Employee::getId);
            case NAME -> Comparator.comparing(Employee::getName);
            case EMAIL -> Comparator.comparing(Employee::getEmail);
            case DEPARTMENT -> Comparator.comparing(Employee::getDepartment);
        };
        if (query.getSortBy() != EmployeeSortField.ID) {
            comparator = comparator.thenComparing(Employee::getId);
        }
        if (query.getDirection() == SortDirection.DESC) {
            comparator = comparator.reversed();
        }
        return comparator;
    }
}