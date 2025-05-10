package com.hospital.dao.impl;

import com.hospital.dao.DoctorDao;
import com.hospital.model.Doctor;
import com.hospital.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDaoImpl implements DoctorDao {
    
    @Override
    public int add(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctors (user_id, name, specialization, contact_number, email) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, doctor.getUserId());
            stmt.setString(2, doctor.getName());
            stmt.setString(3, doctor.getSpecialization());
            stmt.setString(4, doctor.getContactNumber());
            stmt.setString(5, doctor.getEmail());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating doctor failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating doctor failed, no ID obtained.");
            }
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public Doctor getById(int id) throws SQLException {
        String sql = "SELECT * FROM doctors WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractDoctorFromResultSet(rs);
            }
            return null;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Doctor> getAll() throws SQLException {
        String sql = "SELECT * FROM doctors";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Doctor> doctors = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
            }
            return doctors;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(Doctor doctor) throws SQLException {
        String sql = "UPDATE doctors SET user_id = ?, name = ?, specialization = ?, contact_number = ?, email = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctor.getUserId());
            stmt.setString(2, doctor.getName());
            stmt.setString(3, doctor.getSpecialization());
            stmt.setString(4, doctor.getContactNumber());
            stmt.setString(5, doctor.getEmail());
            stmt.setInt(6, doctor.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM doctors WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
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
    public Doctor getByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM doctors WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractDoctorFromResultSet(rs);
            }
            return null;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    private Doctor extractDoctorFromResultSet(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getInt("id"));
        doctor.setUserId(rs.getInt("user_id"));
        doctor.setName(rs.getString("name"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setContactNumber(rs.getString("contact_number"));
        doctor.setEmail(rs.getString("email"));
        return doctor;
    }
    
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) throws SQLException {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
    }
} 