package com.hospital.model;

import java.util.Date;
import java.math.BigDecimal;

public class Bill {
    private int id;
    private int patientId;
    private BigDecimal amount;
    private String description;
    private Date billDate;
    private Status status;
    
    // Additional field for display purposes
    private String patientName;
    
    public enum Status {
        PAID, UNPAID, PARTIAL
    }
    
    public Bill() {
    }
    
    public Bill(int id, int patientId, BigDecimal amount, String description, Date billDate, Status status) {
        this.id = id;
        this.patientId = patientId;
        this.amount = amount;
        this.description = description;
        this.billDate = billDate;
        this.status = status;
    }
    
    public Bill(int patientId, BigDecimal amount, String description, Date billDate) {
        this.patientId = patientId;
        this.amount = amount;
        this.description = description;
        this.billDate = billDate;
        this.status = Status.UNPAID;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getBillDate() {
        return billDate;
    }
    
    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", patientName='" + patientName + '\'' +
                ", amount=" + amount +
                ", billDate=" + billDate +
                ", status=" + status +
                '}';
    }
} 