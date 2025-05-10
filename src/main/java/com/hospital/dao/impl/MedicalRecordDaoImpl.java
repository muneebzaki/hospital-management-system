package com.hospital.dao.impl;

import com.hospital.dao.MedicalRecordDao;
import com.hospital.model.MedicalRecord;
import com.hospital.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDaoImpl implements MedicalRecordDao {
    
    @Override
    public int add(MedicalRecord medicalRecord) throws SQLException {
        String sql = "INSERT INTO medical_records (patient_id, doctor_id, diagnosis, treatment, prescription) " +
                    "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, medicalRecord.getPatientId());
            stmt.setInt(2, medicalRecord.getDoctorId());
            stmt.setString(3, medicalRecord.getDiagnosis());
            stmt.setString(4, medicalRecord.getTreatment());
            stmt.setString(5, medicalRecord.getPrescription());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating medical record failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating medical record failed, no ID obtained.");
            }
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public MedicalRecord getById(int id) throws SQLException {
        String sql = "SELECT mr.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM medical_records mr " +
                    "JOIN patients p ON mr.patient_id = p.id " +
                    "JOIN doctors d ON mr.doctor_id = d.id " +
                    "WHERE mr.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractMedicalRecordFromResultSet(rs);
            }
            return null;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public List<MedicalRecord> getAll() throws SQLException {
        String sql = "SELECT mr.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM medical_records mr " +
                    "JOIN patients p ON mr.patient_id = p.id " +
                    "JOIN doctors d ON mr.doctor_id = d.id";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                medicalRecords.add(extractMedicalRecordFromResultSet(rs));
            }
            return medicalRecords;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(MedicalRecord medicalRecord) throws SQLException {
        String sql = "UPDATE medical_records SET patient_id = ?, doctor_id = ?, diagnosis = ?, " +
                    "treatment = ?, prescription = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, medicalRecord.getPatientId());
            stmt.setInt(2, medicalRecord.getDoctorId());
            stmt.setString(3, medicalRecord.getDiagnosis());
            stmt.setString(4, medicalRecord.getTreatment());
            stmt.setString(5, medicalRecord.getPrescription());
            stmt.setInt(6, medicalRecord.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM medical_records WHERE id = ?";
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
    public List<MedicalRecord> getByPatientId(int patientId) throws SQLException {
        String sql = "SELECT mr.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM medical_records mr " +
                    "JOIN patients p ON mr.patient_id = p.id " +
                    "JOIN doctors d ON mr.doctor_id = d.id " +
                    "WHERE mr.patient_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                medicalRecords.add(extractMedicalRecordFromResultSet(rs));
            }
            return medicalRecords;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public List<MedicalRecord> getByDoctorId(int doctorId) throws SQLException {
        String sql = "SELECT mr.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM medical_records mr " +
                    "JOIN patients p ON mr.patient_id = p.id " +
                    "JOIN doctors d ON mr.doctor_id = d.id " +
                    "WHERE mr.doctor_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                medicalRecords.add(extractMedicalRecordFromResultSet(rs));
            }
            return medicalRecords;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    private MedicalRecord extractMedicalRecordFromResultSet(ResultSet rs) throws SQLException {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setId(rs.getInt("id"));
        medicalRecord.setPatientId(rs.getInt("patient_id"));
        medicalRecord.setDoctorId(rs.getInt("doctor_id"));
        medicalRecord.setDiagnosis(rs.getString("diagnosis"));
        medicalRecord.setTreatment(rs.getString("treatment"));
        medicalRecord.setPrescription(rs.getString("prescription"));
        
        Timestamp recordDate = rs.getTimestamp("record_date");
        if (recordDate != null) {
            medicalRecord.setRecordDate(new java.util.Date(recordDate.getTime()));
        }
        
        // Set additional display fields
        medicalRecord.setPatientName(rs.getString("patient_name"));
        medicalRecord.setDoctorName(rs.getString("doctor_name"));
        
        return medicalRecord;
    }
    
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) throws SQLException {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
    }
} 