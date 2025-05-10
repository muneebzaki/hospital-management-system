package com.hospital.dao;

import com.hospital.model.Doctor;
import java.sql.SQLException;
import java.util.List;

public interface DoctorDao {
    int add(Doctor doctor) throws SQLException;
    Doctor getById(int id) throws SQLException;
    List<Doctor> getAll() throws SQLException;
    boolean update(Doctor doctor) throws SQLException;
    boolean delete(int id) throws SQLException;
    Doctor getByUserId(int userId) throws SQLException;
} 