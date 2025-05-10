package com.hospital.dao;

import com.hospital.model.Patient;
import java.sql.SQLException;
import java.util.List;

public interface PatientDao {
    int add(Patient patient) throws SQLException;
    Patient getById(int id) throws SQLException;
    List<Patient> getAll() throws SQLException;
    boolean update(Patient patient) throws SQLException;
    boolean delete(int id) throws SQLException;
    List<Patient> searchByName(String name) throws SQLException;
} 