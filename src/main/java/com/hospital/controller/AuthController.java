package com.hospital.controller;

import com.hospital.dao.DaoFactory;
import com.hospital.dao.UserDao;
import com.hospital.model.User;

import java.sql.SQLException;

public class AuthController {
    private static AuthController instance;
    private final UserDao userDao;
    private User currentUser;
    
    private AuthController() {
        userDao = DaoFactory.getInstance().getUserDao();
    }
    
    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }
    
    public boolean login(String username, String password) {
        try {
            User user = userDao.authenticate(username, password);
            if (user != null) {
                currentUser = user;
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean isAdmin() {
        return isLoggedIn() && currentUser.getRole() == User.Role.ADMIN;
    }
    
    public boolean isDoctor() {
        return isLoggedIn() && currentUser.getRole() == User.Role.DOCTOR;
    }
    
    public boolean isNurse() {
        return isLoggedIn() && currentUser.getRole() == User.Role.NURSE;
    }
    
    public boolean registerUser(String username, String password, User.Role role) {
        try {
            User existingUser = userDao.getByUsername(username);
            if (existingUser != null) {
                return false; // Username already exists
            }
            
            User newUser = new User(username, password, role);
            int userId = userDao.add(newUser);
            return userId > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!isLoggedIn()) {
            return false;
        }
        
        try {
            User user = userDao.authenticate(currentUser.getUsername(), oldPassword);
            if (user != null) {
                user.setPassword(newPassword);
                return userDao.update(user);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 