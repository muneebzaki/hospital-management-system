package com.hospital.dao.impl;

import com.hospital.dao.AppointmentDao;
import com.hospital.model.Appointment;
import com.hospital.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentDaoImpl implements AppointmentDao {
    
    @Override
    public int add(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, start_time, end_time, status, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, new java.sql.Date(appointment.getAppointmentDate().getTime()));
            stmt.setTime(4, appointment.getStartTime());
            stmt.setTime(5, appointment.getEndTime());
            stmt.setString(6, appointment.getStatus().name());
            stmt.setString(7, appointment.getNotes());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating appointment failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating appointment failed, no ID obtained.");
            }
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public Appointment getById(int id) throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "WHERE a.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAppointmentFromResultSet(rs);
            }
            return null;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Appointment> getAll() throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
            return appointments;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, appointment_date = ?, " +
                    "start_time = ?, end_time = ?, status = ?, notes = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, new java.sql.Date(appointment.getAppointmentDate().getTime()));
            stmt.setTime(4, appointment.getStartTime());
            stmt.setTime(5, appointment.getEndTime());
            stmt.setString(6, appointment.getStatus().name());
            stmt.setString(7, appointment.getNotes());
            stmt.setInt(8, appointment.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id = ?";
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
    public List<Appointment> getByPatientId(int patientId) throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "WHERE a.patient_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
            return appointments;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Appointment> getByDoctorId(int doctorId) throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "WHERE a.doctor_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
            return appointments;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Appointment> getByDate(Date date) throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "WHERE a.appointment_date = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
            return appointments;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean hasOverlappingAppointment(int doctorId, Date date, Time startTime, Time endTime, Integer excludeAppointmentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments " +
                    "WHERE doctor_id = ? AND appointment_date = ? AND status != 'CANCELLED' " +
                    "AND ((start_time <= ? AND end_time > ?) OR (start_time < ? AND end_time >= ?) OR (start_time >= ? AND end_time <= ?))";
        
        if (excludeAppointmentId != null) {
            sql += " AND id != ?";
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            stmt.setDate(2, new java.sql.Date(date.getTime()));
            stmt.setTime(3, startTime);
            stmt.setTime(4, startTime);
            stmt.setTime(5, endTime);
            stmt.setTime(6, endTime);
            stmt.setTime(7, startTime);
            stmt.setTime(8, endTime);
            
            if (excludeAppointmentId != null) {
                stmt.setInt(9, excludeAppointmentId);
            }
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean updateStatus(int id, Appointment.Status status) throws SQLException {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
        }
    }
    
    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(rs.getInt("id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        
        Date appointmentDate = rs.getDate("appointment_date");
        if (appointmentDate != null) {
            appointment.setAppointmentDate(new java.util.Date(appointmentDate.getTime()));
        }
        
        appointment.setStartTime(rs.getTime("start_time"));
        appointment.setEndTime(rs.getTime("end_time"));
        appointment.setStatus(Appointment.Status.valueOf(rs.getString("status")));
        appointment.setNotes(rs.getString("notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            appointment.setCreatedAt(new java.util.Date(createdAt.getTime()));
        }
        
        // Set additional display fields
        appointment.setPatientName(rs.getString("patient_name"));
        appointment.setDoctorName(rs.getString("doctor_name"));
        
        return appointment;
    }
    
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) throws SQLException {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
    }
} 