-- Create the hospital database
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'DOCTOR', 'NURSE') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create doctors table
CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20),
    email VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create patients table
CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    date_of_birth DATE,
    blood_group VARCHAR(5),
    contact_number VARCHAR(20),
    address TEXT,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    doctor_id INT,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- Create medical_records table
CREATE TABLE IF NOT EXISTS medical_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    doctor_id INT,
    diagnosis TEXT,
    treatment TEXT,
    prescription TEXT,
    record_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- Create bills table
CREATE TABLE IF NOT EXISTS bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    description TEXT,
    bill_date DATE NOT NULL,
    status ENUM('PAID', 'UNPAID', 'PARTIAL') DEFAULT 'UNPAID',
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Insert default admin user
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'ADMIN');

-- Insert some sample doctors
INSERT INTO users (username, password, role) VALUES 
('doctor1', 'doctor123', 'DOCTOR'),
('doctor2', 'doctor123', 'DOCTOR');

INSERT INTO doctors (user_id, name, specialization, contact_number, email) VALUES 
(2, 'Dr. John Smith', 'Cardiology', '555-1234', 'john.smith@hospital.com'),
(3, 'Dr. Sarah Johnson', 'Neurology', '555-5678', 'sarah.johnson@hospital.com');

-- Insert a nurse user
INSERT INTO users (username, password, role) VALUES 
('nurse1', 'nurse123', 'NURSE'); 