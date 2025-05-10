package com.hospital.controller;

import com.hospital.dao.BillDao;
import com.hospital.dao.DaoFactory;
import com.hospital.model.Bill;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class BillController {
    private static BillController instance;
    private final BillDao billDao;
    
    private BillController() {
        billDao = DaoFactory.getInstance().getBillDao();
    }
    
    public static BillController getInstance() {
        if (instance == null) {
            instance = new BillController();
        }
        return instance;
    }
    
    public boolean generateBill(int patientId, BigDecimal amount, String description, Date billDate) {
        try {
            Bill bill = new Bill(patientId, amount, description, billDate);
            int billId = billDao.add(bill);
            return billId > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Bill getBillById(int id) {
        try {
            return billDao.getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Bill> getAllBills() {
        try {
            return billDao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Bill> getBillsByPatientId(int patientId) {
        try {
            return billDao.getByPatientId(patientId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateBill(Bill bill) {
        try {
            return billDao.update(bill);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean markBillAsPaid(int id) {
        try {
            return billDao.updateStatus(id, Bill.Status.PAID);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean markBillAsPartiallyPaid(int id) {
        try {
            return billDao.updateStatus(id, Bill.Status.PARTIAL);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteBill(int id) {
        try {
            return billDao.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 