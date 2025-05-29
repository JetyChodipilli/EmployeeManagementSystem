package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    @Query("SELECT e FROM Employee e WHERE " +
           "(:employeeId IS NULL OR LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :employeeId, '%'))) " +
           "AND (:firstName IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
           "AND (:lastName IS NULL OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
           "AND (:loginId IS NULL OR LOWER(e.loginId) LIKE LOWER(CONCAT('%', :loginId, '%'))) " +
           "AND (:department IS NULL OR e.department = :department) " +
           "AND (:startDate IS NULL OR e.dateOfBirth >= :startDate) " +
           "AND (:endDate IS NULL OR e.dateOfBirth <= :endDate)")
    Page<Employee> searchEmployees(
            @Param("employeeId") String employeeId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("loginId") String loginId,
            @Param("department") String department,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    boolean existsByLoginId(String loginId);
}
