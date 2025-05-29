package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping({"/login", "/login.html"})
    public String showLoginPage() {
        return "login";
    }

    @GetMapping({"/employee/add", "/add-employee.html"})
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "add-employee";
    }

    @PostMapping("/employee/add")
    public String addEmployee(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam(value = "middleName", required = false) String middleName,
            @RequestParam("dateOfBirth") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateOfBirth,
            @RequestParam("department") String department,
            @RequestParam("salary") double salary,
            @RequestParam("permanentAddress") String permanentAddress,
            @RequestParam("currentAddress") String currentAddress,
            @RequestParam(value = "idProof", required = false) MultipartFile idProof,
            Model model,
            HttpSession session) {
        try {
            if (idProof == null || idProof.isEmpty()) {
                throw new IllegalArgumentException("ID Proof file is required");
            }
            Employee employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setMiddleName(middleName);
            employee.setDateOfBirth(dateOfBirth);
            employee.setDepartment(department);
            employee.setSalary(salary);
            employee.setPermanentAddress(permanentAddress);
            employee.setCurrentAddress(currentAddress);
            employeeService.addEmployee(employee, idProof);
            return redirectToSearchResults(session, model);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "add-employee";
        }
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exc, Model model) {
        model.addAttribute("error", "File size exceeds the maximum limit of 1MB. Please upload a file between 10KB and 1MB.");
        return "add-employee";
    }

    @GetMapping({"/employee/search", "/search-employee.html"})
    public String showSearchPage(Model model) {
        model.addAttribute("employee", new Employee());
        return "search-employee";
    }

    @GetMapping({"/employee/results", "/search-results.html"})
    public String searchEmployees(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String loginId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            HttpSession session) {
        // Store search criteria in session
        session.setAttribute("searchEmployeeId", employeeId);
        session.setAttribute("searchFirstName", firstName);
        session.setAttribute("searchLastName", lastName);
        session.setAttribute("searchLoginId", loginId);
        session.setAttribute("searchDepartment", department);
        session.setAttribute("searchStartDate", startDate);
        session.setAttribute("searchEndDate", endDate);

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeService.searchEmployees(
                employeeId, firstName, lastName, loginId, department, startDate, endDate, pageable);
        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("currentPage", employeePage.getNumber());
        model.addAttribute("totalPages", employeePage.getTotalPages());
        return "search-results";
    }

    @GetMapping({"/employee/view/{id}", "/view-employee.html/{id}"})
    public String viewEmployee(@PathVariable String id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "view-employee";
    }

    @GetMapping({"/employee/edit/{id}", "/edit-employee.html/{id}"})
    public String showEditForm(@PathVariable String id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "edit-employee";
    }

    @PostMapping("/employee/edit/{id}")
    public String updateEmployee(@PathVariable String id,
                                 @RequestParam(value = "idProof", required = false) MultipartFile idProof,
                                 Model model,
                                 HttpSession session) {
        try {
            if (idProof == null || idProof.isEmpty()) {
                model.addAttribute("error", "No file selected for ID Proof update");
                return "edit-employee";
            }
            employeeService.updateEmployee(id, idProof);
            return redirectToSearchResults(session, model);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "edit-employee";
        }
    }

    @PostMapping("/employee/delete")
    public String deleteEmployees(
            @RequestParam(value = "employeeIds", required = false) List<String> employeeIds,
            @RequestParam(value = "formSubmitted", required = false) String formSubmitted,
            Model model,
            HttpSession session) {
        if ("true".equals(formSubmitted) && (employeeIds == null || employeeIds.isEmpty())) {
            model.addAttribute("error", "Please select at least one employee to delete.");
            return redirectToSearchResults(session, model);
        }
        if (employeeIds == null || employeeIds.isEmpty()) {
            return redirectToSearchResults(session, model);
        }
        employeeService.deleteEmployees(employeeIds);
        return redirectToSearchResults(session, model);
    }

    @GetMapping("/employee/delete")
    public String handleInvalidDelete(Model model, HttpSession session) {
        model.addAttribute("error", "Invalid delete request. Please use the search page to delete employees.");
        return redirectToSearchResults(session, model);
    }

    private String redirectToSearchResults(HttpSession session, Model model) {
        String employeeId = (String) session.getAttribute("searchEmployeeId");
        String firstName = (String) session.getAttribute("searchFirstName");
        String lastName = (String) session.getAttribute("searchLastName");
        String loginId = (String) session.getAttribute("searchLoginId");
        String department = (String) session.getAttribute("searchDepartment");
        LocalDate startDate = (LocalDate) session.getAttribute("searchStartDate");
        LocalDate endDate = (LocalDate) session.getAttribute("searchEndDate");

        StringBuilder redirectUrl = new StringBuilder("redirect:/employee/results?page=0");
        if (employeeId != null && !employeeId.isEmpty()) {
            redirectUrl.append("&employeeId=").append(employeeId);
        }
        if (firstName != null && !firstName.isEmpty()) {
            redirectUrl.append("&firstName=").append(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            redirectUrl.append("&lastName=").append(lastName);
        }
        if (loginId != null && !loginId.isEmpty()) {
            redirectUrl.append("&loginId=").append(loginId);
        }
        if (department != null && !department.isEmpty()) {
            redirectUrl.append("&department=").append(department);
        }
        if (startDate != null) {
            redirectUrl.append("&startDate=").append(startDate.toString());
        }
        if (endDate != null) {
            redirectUrl.append("&endDate=").append(endDate.toString());
        }
        return redirectUrl.toString();
    }
}
