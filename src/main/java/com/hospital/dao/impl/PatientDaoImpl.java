package com.hospital.dao.impl;

import com.hospital.dao.PatientDao;
import com.hospital.model.Patient;
import com.hospital.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDaoImpl implements PatientDao {
    
    @Override
    public int add(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (name, gender, date_of_birth, blood_group, contact_number, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getGender().name());
            
            if (patient.getDateOfBirth() != null) {
                stmt.setDate(3, new java.sql.Date(patient.getDateOfBirth().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            
            stmt.setString(4, patient.getBloodGroup());
            stmt.setString(5, patient.getContactNumber());
            stmt.setString(6, patient.getAddress());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating patient failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating patient failed, no ID obtained.");
            }
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public Patient getById(int id) throws SQLException {
        String sql = "SELECT * FROM patients WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPatientFromResultSet(rs);
            }
            return null;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Patient> getAll() throws SQLException {
        String sql = "SELECT * FROM patients";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Patient> patients = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
            return patients;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET name = ?, gender = ?, date_of_birth = ?, " +
                    "blood_group = ?, contact_number = ?, address = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getGender().name());
            
            if (patient.getDateOfBirth() != null) {
                stmt.setDate(3, new java.sql.Date(patient.getDateOfBirth().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            
            stmt.setString(4, patient.getBloodGroup());
            stmt.setString(5, patient.getContactNumber());
            stmt.setString(6, patient.getAddress());
            stmt.setInt(7, patient.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM patients WHERE id = ?";
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
    public List<Patient> searchByName(String name) throws SQLException {
        String sql = "SELECT * FROM patients WHERE name LIKE ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Patient> patients = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + name + "%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
            return patients;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("id"));
        patient.setName(rs.getString("name"));
        patient.setGender(Patient.Gender.valueOf(rs.getString("gender")));
        
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            patient.setDateOfBirth(new java.util.Date(dob.getTime()));
        }
        
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setContactNumber(rs.getString("contact_number"));
        patient.setAddress(rs.getString("address"));
        
        Timestamp regDate = rs.getTimestamp("registration_date");
        if (regDate != null) {
            patient.setRegistrationDate(new java.util.Date(regDate.getTime()));
        }
        
        return patient;
    }
    
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) throws SQLException {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
    }
} 