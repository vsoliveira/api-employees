package com.company.employees.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for EmployeeEntity.
 * Internal to infrastructure layer.
 */
@Repository
public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, UUID> {
    Optional<EmployeeEntity> findByEmail(String email);
}
