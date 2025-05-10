package com.hospital.dao;

import com.hospital.model.Bill;
import java.sql.SQLException;
import java.util.List;

public interface BillDao {
    int add(Bill bill) throws SQLException;
    Bill getById(int id) throws SQLException;
    List<Bill> getAll() throws SQLException;
    boolean update(Bill bill) throws SQLException;
    boolean delete(int id) throws SQLException;
    List<Bill> getByPatientId(int patientId) throws SQLException;
    boolean updateStatus(int id, Bill.Status status) throws SQLException;
} 