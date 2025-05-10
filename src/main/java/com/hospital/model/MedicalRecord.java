package com.hospital.model;

import java.util.Date;

public class MedicalRecord {
    private int id;
    private int patientId;
    private int doctorId;
    private String diagnosis;
    private String treatment;
    private String prescription;
    private Date recordDate;
    
    // Additional fields for display purposes
    private String patientName;
    private String doctorName;
    
    public MedicalRecord() {
    }
    
    public MedicalRecord(int id, int patientId, int doctorId, String diagnosis, 
                        String treatment, String prescription, Date recordDate) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.prescription = prescription;
        this.recordDate = recordDate;
    }
    
    public MedicalRecord(int patientId, int doctorId, String diagnosis, 
                        String treatment, String prescription) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.prescription = prescription;
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
    
    public int getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public String getTreatment() {
        return treatment;
    }
    
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
    
    public String getPrescription() {
        return prescription;
    }
    
    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }
    
    public Date getRecordDate() {
        return recordDate;
    }
    
    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    @Override
    public String toString() {
        return "MedicalRecord{" +
                "id=" + id +
                ", patientName='" + patientName + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", recordDate=" + recordDate +
                '}';
    }
} 