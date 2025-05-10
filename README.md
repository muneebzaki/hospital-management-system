# Hospital Management System

A simple desktop application for hospital management built with Java Swing and MySQL.

## Features

- **Role-Based Access Control**: Admin, Doctor, and Nurse roles with different permissions
- **Patient Management**: Add, view, edit, and delete patient records
- **Doctor Management**: Register doctors with specializations and availability
- **Appointment Booking**: Schedule, cancel, and track appointments
- **Medical Records**: Store and retrieve patient diagnosis and treatment information
- **Billing System**: Generate and manage patient bills

## Technologies Used

- Java 8+
- Java Swing for GUI
- MySQL for database
- JDBC for database connectivity
- Maven for build management
- JUnit 5 for unit testing

## Architecture

The application follows the MVC (Model-View-Controller) + DAO (Data Access Object) design pattern:

- **Model**: Represents the data and business logic
- **View**: User interface components
- **Controller**: Handles user input and updates the model and view
- **DAO**: Provides an abstract interface for database operations

## Prerequisites

- JDK 8 or higher
- MySQL 5.7 or higher
- Maven 3.6 or higher

## Setup Instructions

### Database Setup

1. Install MySQL if not already installed
2. Create a database named `hospital_db`
3. Run the SQL script located at `src/main/resources/db/hospital_db.sql` to create tables and insert sample data

### Configuration

1. Open `src/main/java/com/hospital/util/DatabaseUtil.java`
2. Update the database connection details (URL, username, password) if needed

### Building the Application

```bash
mvn clean package
```

### Running the Application

```bash
java -jar target/hospital-management-system-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Default Login Credentials

- **Admin**: username: admin, password: admin123
- **Doctor**: username: doctor1, password: doctor123
- **Nurse**: username: nurse1, password: nurse123

## Project Structure

```
hospital-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── hospital/
│   │   │           ├── controller/    # Controllers
│   │   │           ├── dao/           # Data Access Objects
│   │   │           │   └── impl/      # DAO implementations
│   │   │           ├── model/         # Model classes
│   │   │           ├── util/          # Utility classes
│   │   │           ├── view/          # Swing UI components
│   │   │           └── Main.java      # Application entry point
│   │   └── resources/
│   │       └── db/                    # Database scripts
│   └── test/
│       └── java/
│           └── com/
│               └── hospital/
│                   ├── controller/    # Controller tests
│                   ├── dao/           # DAO tests
│                   └── model/         # Model tests
└── pom.xml                            # Maven configuration
```

## Testing

Run tests using Maven:

```bash
mvn test
```

## License

This project is licensed under the MIT License. 