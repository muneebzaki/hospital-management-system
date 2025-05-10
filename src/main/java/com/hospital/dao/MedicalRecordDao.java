package com.hospital.dao;

import com.hospital.model.MedicalRecord;
import java.sql.SQLException;
import java.util.List;

public interface MedicalRecordDao {
    int add(MedicalRecord medicalRecord) throws SQLException;
    MedicalRecord getById(int id) throws SQLException;
    List<MedicalRecord> getAll() throws SQLException;
    boolean update(MedicalRecord medicalRecord) throws SQLException;
    boolean delete(int id) throws SQLException;
    List<MedicalRecord> getByPatientId(int patientId) throws SQLException;
    List<MedicalRecord> getByDoctorId(int doctorId) throws SQLException;
} 