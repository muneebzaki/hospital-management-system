package com.hospital.dao;

import com.hospital.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    User authenticate(String username, String password) throws SQLException;
    int add(User user) throws SQLException;
    User getById(int id) throws SQLException;
    List<User> getAll() throws SQLException;
    boolean update(User user) throws SQLException;
    boolean delete(int id) throws SQLException;
    User getByUsername(String username) throws SQLException;
} 