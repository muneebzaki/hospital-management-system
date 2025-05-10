package com.hospital.controller;

import com.hospital.dao.AppointmentDao;
import com.hospital.dao.DaoFactory;
import com.hospital.model.Appointment;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.List;

public class AppointmentController {
    private static AppointmentController instance;
    private final AppointmentDao appointmentDao;
    
    private AppointmentController() {
        appointmentDao = DaoFactory.getInstance().getAppointmentDao();
    }
    
    public static AppointmentController getInstance() {
        if (instance == null) {
            instance = new AppointmentController();
        }
        return instance;
    }
    
    public boolean bookAppointment(int patientId, int doctorId, Date appointmentDate, 
                                 Time startTime, Time endTime, String notes) {
        try {
            // Check for overlapping appointments
            if (appointmentDao.hasOverlappingAppointment(doctorId, appointmentDate, startTime, endTime, null)) {
                return false; // Overlapping appointment exists
            }
            
            Appointment appointment = new Appointment(patientId, doctorId, appointmentDate, startTime, endTime, notes);
            int appointmentId = appointmentDao.add(appointment);
            return appointmentId > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Appointment getAppointmentById(int id) {
        try {
            return appointmentDao.getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Appointment> getAllAppointments() {
        try {
            return appointmentDao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        try {
            return appointmentDao.getByPatientId(patientId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Appointment> getAppointmentsByDoctorId(int doctorId) {
        try {
            return appointmentDao.getByDoctorId(doctorId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Appointment> getAppointmentsByDate(Date date) {
        try {
            return appointmentDao.getByDate(date);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateAppointment(Appointment appointment) {
        try {
            // Check for overlapping appointments (excluding the current appointment)
            if (appointmentDao.hasOverlappingAppointment(appointment.getDoctorId(), 
                    appointment.getAppointmentDate(), appointment.getStartTime(), 
                    appointment.getEndTime(), appointment.getId())) {
                return false; // Overlapping appointment exists
            }
            
            return appointmentDao.update(appointment);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean cancelAppointment(int id) {
        try {
            return appointmentDao.updateStatus(id, Appointment.Status.CANCELLED);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean completeAppointment(int id) {
        try {
            return appointmentDao.updateStatus(id, Appointment.Status.COMPLETED);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteAppointment(int id) {
        try {
            return appointmentDao.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 