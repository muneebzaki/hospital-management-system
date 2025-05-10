package com.hospital.dao.impl;

import com.hospital.dao.UserDao;
import com.hospital.model.User;
import com.hospital.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    
    @Override
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            return null;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public int add(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            return null;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public List<User> getAll() throws SQLException {
        String sql = "SELECT * FROM users";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            return users;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
        }
    }
    
    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());
            stmt.setInt(4, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
        }
    }
    
    @Override
    public User getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            return null;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        return user;
    }
    
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) throws SQLException {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
    }
    
    // Method made protected to allow overriding in tests
    protected Connection getConnection() throws SQLException {
        return DatabaseUtil.getInstance().getConnection();
    }
} 