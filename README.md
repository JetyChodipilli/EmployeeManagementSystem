# Employee Management System

## Overview

The **Employee Management System** is a Spring Boot application designed to manage employee records efficiently. It provides functionalities to add, update, delete, search, and view employee details, along with maintaining a history of actions performed on employee records. The application also supports uploading and viewing ID proof PDFs and generating employee detail reports in PDF format.

This project uses Spring MVC, Spring Data JPA, Hibernate, Thymeleaf for templating, and iText for PDF generation. It is secured with basic Spring Security (login page) and connects to a MySQL database for persistent storage.

## Features

- **Add Employee**: Add a new employee with details such as name, date of birth, department, salary, addresses, and an ID proof (PDF file, 10KB–1MB).
- **Search Employees**: Search employees by various criteria (employee ID, name, login ID, department, date of birth range) with pagination support.
- **View Employee**: View detailed employee information, including their history of actions and a link to their ID proof PDF.
- **Update Employee**: Update an employee's ID proof.
- **Delete Employee(s)**: Delete one or multiple employees, with error handling for non-existent employees.
- **Employee History**: Automatically track actions (create, update, delete) with timestamps.
- **PDF Generation**: Download employee details, including their history, as a PDF.
- **File Upload**: Upload and store employee ID proofs in PDF format.
- **Validation**: Age validation (must be 18+), file type (PDF only), and file size (10KB–1MB) checks.
- **Responsive UI**: Bootstrap-based UI for a clean and responsive user experience.

## Technologies Used

- **Java**: 17
- **Spring Boot**: 3.3.4
- **Spring MVC**: For handling web requests
- **Spring Data JPA**: For database operations
- **Hibernate**: As the JPA provider
- **Thymeleaf**: For server-side templating
- **MySQL**: Database for persistent storage
- **iText**: For PDF generation
- **Bootstrap**: 5.3.3 (via Webjars) for styling
- **jQuery**: 3.7.1 (via Webjars) for client-side scripting
- **Maven**: For dependency management

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17** or later
- **Maven** (for building the project)
- **MySQL** (for the database)
- A web browser (e.g., Chrome, Firefox)

## Setup Instructions

### 1. Clone the Repository
Clone the repository to your local machine:

```bash
git clone https://github.com/your-username/employee-management-system.git
cd employee-management-system
```

### 2. Configure the Database
- Install MySQL if not already installed.
- Create a database named `employee_db`:

```sql
CREATE DATABASE employee_db;
```

- Update the database configuration in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# File upload size limits
spring.servlet.multipart.max-file-size=1MB
spring.servlet.multipart.max-request-size=1MB

# File storage location
file.upload-dir=uploads/
```

Replace `your_username` and `your_password` with your MySQL credentials.

### 3. Build the Project
Navigate to the project root directory and build the project using Maven:

```bash
mvn clean install
```

### 4. Run the Application
Run the Spring Boot application:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

### 5. Access the Application
- Open your browser and navigate to `http://localhost:8080/login`.
- Use the default credentials (configured in Spring Security, if applicable) or bypass the login page if security is not fully implemented.
- Start managing employees!

## Project Structure

```
employee-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/employeemanagement/
│   │   │       ├── controller/      # Spring MVC controllers
│   │   │       ├── model/           # Entity classes (Employee, EmployeeHistory)
│   │   │       ├── repository/      # Spring Data JPA repositories
│   │   │       ├── service/         # Business logic (EmployeeService, PdfGenerationService)
│   │   │       └── EmployeeManagementApplication.java  # Main application class
│   │   ├── resources/
│   │   │   ├── templates/           # Thymeleaf templates (HTML)
│   │   │   ├── static/              # Static resources (CSS, JS)
│   │   │   └── application.properties  # Configuration file
│   └── test/                        # Test classes
├── uploads/                         # Directory for storing uploaded ID proofs
├── pom.xml                          # Maven dependencies and build configuration
└── README.md                        # This file
```

## Usage

### 1. Add an Employee
- Navigate to `http://localhost:8080/employee/add`.
- Fill in the employee details and upload an ID proof (PDF file).
- Submit the form to add the employee.

### 2. Search for Employees
- Go to `http://localhost:8080/employee/search`.
- Enter search criteria (e.g., employee ID, name, department).
- View the paginated results on the search results page.

### 3. View Employee Details
- From the search results, click on an employee’s ID to view their details.
- View their history and download their ID proof if available.
- Click "Download PDF" to generate a PDF report of the employee’s details.

### 4. Update an Employee
- From the search results, click "Edit" next to an employee.
- Upload a new ID proof PDF to update the employee’s record.

### 5. Delete Employees
- On the search results page, select one or more employees using the checkboxes.
- Click "Delete Selected" to remove them.
- Errors (e.g., if an employee is not found) will be displayed on the page.

## Screenshots

*(You can add screenshots of the application here. Use a tool to capture the UI and upload the images to your repository, then link them here.)*

## Known Issues

- Spring Security is partially implemented; the login page exists but may not be fully functional.
- Limited error handling for edge cases (e.g., duplicate login IDs in rare scenarios).
- The UI could be enhanced with more interactive features (e.g., AJAX for search).

## Future Improvements

- Fully implement Spring Security with user roles (admin, manager, etc.).
- Add more validation for employee fields (e.g., email format, phone number).
- Support for bulk upload of employees via CSV.
- Enhance the PDF report with better formatting and styling.
- Add unit and integration tests.

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Make your changes and commit them (`git commit -m "Add your feature"`).
4. Push to your branch (`git push origin feature/your-feature`).
5. Open a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For any questions or feedback, please reach out to [your-email@example.com](mailto:your-email@example.com).
