package com.hospital.dao.impl;

import com.hospital.dao.BillDao;
import com.hospital.model.Bill;
import com.hospital.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDaoImpl implements BillDao {
    
    @Override
    public int add(Bill bill) throws SQLException {
        String sql = "INSERT INTO bills (patient_id, amount, description, bill_date, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, bill.getPatientId());
            stmt.setBigDecimal(2, bill.getAmount());
            stmt.setString(3, bill.getDescription());
            stmt.setDate(4, new java.sql.Date(bill.getBillDate().getTime()));
            stmt.setString(5, bill.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating bill failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating bill failed, no ID obtained.");
            }
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public Bill getById(int id) throws SQLException {
        String sql = "SELECT b.*, p.name as patient_name FROM bills b JOIN patients p ON b.patient_id = p.id WHERE b.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractBillFromResultSet(rs);
            }
            return null;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Bill> getAll() throws SQLException {
        String sql = "SELECT b.*, p.name as patient_name FROM bills b JOIN patients p ON b.patient_id = p.id";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Bill> bills = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }
            return bills;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(Bill bill) throws SQLException {
        String sql = "UPDATE bills SET patient_id = ?, amount = ?, description = ?, bill_date = ?, status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bill.getPatientId());
            stmt.setBigDecimal(2, bill.getAmount());
            stmt.setString(3, bill.getDescription());
            stmt.setDate(4, new java.sql.Date(bill.getBillDate().getTime()));
            stmt.setString(5, bill.getStatus().name());
            stmt.setInt(6, bill.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM bills WHERE id = ?";
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
    public List<Bill> getByPatientId(int patientId) throws SQLException {
        String sql = "SELECT b.*, p.name as patient_name FROM bills b JOIN patients p ON b.patient_id = p.id WHERE b.patient_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Bill> bills = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }
            return bills;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean updateStatus(int id, Bill.Status status) throws SQLException {
        String sql = "UPDATE bills SET status = ? WHERE id = ?";
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
    
    private Bill extractBillFromResultSet(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        bill.setPatientId(rs.getInt("patient_id"));
        bill.setAmount(rs.getBigDecimal("amount"));
        bill.setDescription(rs.getString("description"));
        
        Date billDate = rs.getDate("bill_date");
        if (billDate != null) {
            bill.setBillDate(new java.util.Date(billDate.getTime()));
        }
        
        bill.setStatus(Bill.Status.valueOf(rs.getString("status")));
        
        // Set additional display field
        bill.setPatientName(rs.getString("patient_name"));
        
        return bill;
    }
    
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) throws SQLException {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) DatabaseUtil.getInstance().closeConnection(conn);
    }
} 