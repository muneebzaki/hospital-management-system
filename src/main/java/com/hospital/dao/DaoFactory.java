package com.hospital.dao;

import com.hospital.dao.impl.*;

public class DaoFactory {
    private static DaoFactory instance;
    
    private final UserDao userDao;
    private final DoctorDao doctorDao;
    private final PatientDao patientDao;
    private final AppointmentDao appointmentDao;
    private final MedicalRecordDao medicalRecordDao;
    private final BillDao billDao;
    
    private DaoFactory() {
        userDao = new UserDaoImpl();
        doctorDao = new DoctorDaoImpl();
        patientDao = new PatientDaoImpl();
        appointmentDao = new AppointmentDaoImpl();
        medicalRecordDao = new MedicalRecordDaoImpl();
        billDao = new BillDaoImpl();
    }
    
    public static DaoFactory getInstance() {
        if (instance == null) {
            instance = new DaoFactory();
        }
        return instance;
    }
    
    public UserDao getUserDao() {
        return userDao;
    }
    
    public DoctorDao getDoctorDao() {
        return doctorDao;
    }
    
    public PatientDao getPatientDao() {
        return patientDao;
    }
    
    public AppointmentDao getAppointmentDao() {
        return appointmentDao;
    }
    
    public MedicalRecordDao getMedicalRecordDao() {
        return medicalRecordDao;
    }
    
    public BillDao getBillDao() {
        return billDao;
    }
} 