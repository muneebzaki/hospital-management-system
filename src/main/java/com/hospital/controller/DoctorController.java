package com.hospital.controller;

import com.hospital.dao.DaoFactory;
import com.hospital.dao.DoctorDao;
import com.hospital.dao.UserDao;
import com.hospital.model.Doctor;
import com.hospital.model.User;

import java.sql.SQLException;
import java.util.List;

public class DoctorController {
    private static DoctorController instance;
    private final DoctorDao doctorDao;
    private final UserDao userDao;
    
    private DoctorController() {
        doctorDao = DaoFactory.getInstance().getDoctorDao();
        userDao = DaoFactory.getInstance().getUserDao();
    }
    
    public static DoctorController getInstance() {
        if (instance == null) {
            instance = new DoctorController();
        }
        return instance;
    }
    
    public boolean addDoctor(String username, String password, String name, String specialization, 
                           String contactNumber, String email) {
        try {
            // First create a user account
            User user = new User(username, password, User.Role.DOCTOR);
            int userId = userDao.add(user);
            
            if (userId > 0) {
                // Then create doctor profile
                Doctor doctor = new Doctor(userId, name, specialization, contactNumber, email);
                int doctorId = doctorDao.add(doctor);
                return doctorId > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Doctor getDoctorById(int id) {
        try {
            return doctorDao.getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Doctor getDoctorByUserId(int userId) {
        try {
            return doctorDao.getByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Doctor> getAllDoctors() {
        try {
            return doctorDao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateDoctor(Doctor doctor) {
        try {
            return doctorDao.update(doctor);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteDoctor(int id) {
        try {
            Doctor doctor = doctorDao.getById(id);
            if (doctor != null) {
                // Delete the user account first (will cascade to doctor due to foreign key)
                return userDao.delete(doctor.getUserId());
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 