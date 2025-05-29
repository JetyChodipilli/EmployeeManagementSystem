package com.example.employeemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class EmployeeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String action;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) {
}
}
