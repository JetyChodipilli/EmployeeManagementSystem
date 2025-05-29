package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.model.EmployeeHistory;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    private static final String UPLOAD_DIR = "uploads/";

    public Employee addEmployee(Employee employee, MultipartFile idProof) throws IOException {
        // Validate age
        if (Period.between(employee.getDateOfBirth(), LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("Employee must be at least 18 years old");
        }

        // Generate employee ID and login ID
        employee.setEmployeeId(generateEmployeeId());
        employee.setLoginId(generateLoginId(employee.getFirstName(), employee.getLastName()));

        // Handle file upload
        if (idProof != null && !idProof.isEmpty()) {
            validateIdProof(idProof);
            String fileName = UUID.randomUUID() + ".pdf";
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, idProof.getBytes());
            employee.setIdProofPath(fileName);
        }

        // Save the employee first to make it a managed entity
        Employee savedEmployee = employeeRepository.save(employee);

        // Now create and associate the history
        EmployeeHistory history = new EmployeeHistory();
        history.setAction("CREATED");
        history.setTimestamp(LocalDateTime.now());
        history.setEmployee(savedEmployee);
        savedEmployee.getHistory().add(history);

        // Save again to persist the history
        return employeeRepository.save(savedEmployee);
    }

    public Employee updateEmployee(String employeeId, MultipartFile idProof) throws IOException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (idProof != null && !idProof.isEmpty()) {
            validateIdProof(idProof);
            if (employee.getIdProofPath() != null) {
                Files.deleteIfExists(Paths.get(UPLOAD_DIR + employee.getIdProofPath()));
            }
            String fileName = UUID.randomUUID() + ".pdf";
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, idProof.getBytes());
            employee.setIdProofPath(fileName);

            // Update history
            EmployeeHistory history = new EmployeeHistory();
            history.setAction("ID_PROOF_UPDATED");
            history.setTimestamp(LocalDateTime.now());
            history.setEmployee(employee);
            employee.getHistory().add(history);
        }

        return employeeRepository.save(employee);
    }

    public void deleteEmployees(List<String> employeeIds) {
        for (String id : employeeIds) {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
            EmployeeHistory history = new EmployeeHistory();
            history.setAction("DELETED");
            history.setTimestamp(LocalDateTime.now());
            history.setEmployee(employee);
            employee.getHistory().add(history);

            // Save to persist the history before deletion
            employeeRepository.save(employee);

            employeeRepository.deleteById(id);
        }
    }

    // Other methods (searchEmployees, getEmployeeById, etc.) remain unchanged
    public Page<Employee> searchEmployees(String employeeId, String firstName, String lastName,
                                          String loginId, String department, LocalDate startDate, LocalDate endDate,
                                          Pageable pageable) {
        return employeeRepository.searchEmployees(employeeId, firstName, lastName, loginId, department, startDate, endDate, pageable);
    }

    public Employee getEmployeeById(String id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    private String generateEmployeeId() {
        String id;
        do {
            id = String.format("%05d", new Random().nextInt(100000)) + "1";
        } while (employeeRepository.existsById(id));
        return id;
    }

    private String generateLoginId(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name must not be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name must not be null or empty");
        }

        String baseId = (firstName.charAt(0) + "" + lastName.charAt(0)).toLowerCase();
        String candidateLoginId = baseId;
        Random random = new Random();
        int maxAttempts = 1000;
        int attempt = 0;

        while (employeeRepository.existsByLoginId(candidateLoginId) && attempt < maxAttempts) {
            candidateLoginId = baseId + String.format("%03d", random.nextInt(1000));
            attempt++;
        }

        if (attempt >= maxAttempts) {
            throw new IllegalStateException("Unable to generate a unique login ID after " + maxAttempts + " attempts");
        }

        String loginId = candidateLoginId;
        return loginId;
    }

    private void validateIdProof(MultipartFile file) {
        if (!file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
        long sizeInKB = file.getSize() / 1024;
        if (sizeInKB < 10 || sizeInKB > 1024) {
            throw new IllegalArgumentException("File size must be between 10KB and 1MB");
        }
    }
}