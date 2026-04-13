package com.company.employees.infrastructure.persistence;

import com.company.employees.domain.Employee;
import com.company.employees.domain.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<Employee> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean deleteById(UUID id) {
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
