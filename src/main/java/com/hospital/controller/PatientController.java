package com.hospital.controller;

import com.hospital.dao.DaoFactory;
import com.hospital.dao.PatientDao;
import com.hospital.model.Patient;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class PatientController {
    private static PatientController instance;
    private final PatientDao patientDao;
    
    private PatientController() {
        patientDao = DaoFactory.getInstance().getPatientDao();
    }
    
    public static PatientController getInstance() {
        if (instance == null) {
            instance = new PatientController();
        }
        return instance;
    }
    
    public boolean addPatient(String name, Patient.Gender gender, Date dateOfBirth, 
                            String bloodGroup, String contactNumber, String address) {
        try {
            Patient patient = new Patient(name, gender, dateOfBirth, bloodGroup, contactNumber, address);
            int patientId = patientDao.add(patient);
            return patientId > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Patient getPatientById(int id) {
        try {
            return patientDao.getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Patient> getAllPatients() {
        try {
            return patientDao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Patient> searchPatientsByName(String name) {
        try {
            return patientDao.searchByName(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updatePatient(Patient patient) {
        try {
            return patientDao.update(patient);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deletePatient(int id) {
        try {
            return patientDao.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 