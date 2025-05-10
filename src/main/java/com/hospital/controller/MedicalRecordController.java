package com.hospital.controller;

import com.hospital.dao.DaoFactory;
import com.hospital.dao.MedicalRecordDao;
import com.hospital.model.MedicalRecord;

import java.sql.SQLException;
import java.util.List;

public class MedicalRecordController {
    private static MedicalRecordController instance;
    private final MedicalRecordDao medicalRecordDao;
    
    private MedicalRecordController() {
        medicalRecordDao = DaoFactory.getInstance().getMedicalRecordDao();
    }
    
    public static MedicalRecordController getInstance() {
        if (instance == null) {
            instance = new MedicalRecordController();
        }
        return instance;
    }
    
    public boolean addMedicalRecord(int patientId, int doctorId, String diagnosis, String treatment, String prescription) {
        try {
            MedicalRecord medicalRecord = new MedicalRecord(patientId, doctorId, diagnosis, treatment, prescription);
            int recordId = medicalRecordDao.add(medicalRecord);
            return recordId > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public MedicalRecord getMedicalRecordById(int id) {
        try {
            return medicalRecordDao.getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<MedicalRecord> getAllMedicalRecords() {
        try {
            return medicalRecordDao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<MedicalRecord> getMedicalRecordsByPatientId(int patientId) {
        try {
            return medicalRecordDao.getByPatientId(patientId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<MedicalRecord> getMedicalRecordsByDoctorId(int doctorId) {
        try {
            return medicalRecordDao.getByDoctorId(doctorId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateMedicalRecord(MedicalRecord medicalRecord) {
        try {
            return medicalRecordDao.update(medicalRecord);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteMedicalRecord(int id) {
        try {
            return medicalRecordDao.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 