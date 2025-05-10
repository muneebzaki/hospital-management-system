package com.hospital.dao;

import com.hospital.model.Appointment;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface AppointmentDao {
    int add(Appointment appointment) throws SQLException;
    Appointment getById(int id) throws SQLException;
    List<Appointment> getAll() throws SQLException;
    boolean update(Appointment appointment) throws SQLException;
    boolean delete(int id) throws SQLException;
    List<Appointment> getByPatientId(int patientId) throws SQLException;
    List<Appointment> getByDoctorId(int doctorId) throws SQLException;
    List<Appointment> getByDate(Date date) throws SQLException;
    boolean hasOverlappingAppointment(int doctorId, Date date, java.sql.Time startTime, java.sql.Time endTime, Integer excludeAppointmentId) throws SQLException;
    boolean updateStatus(int id, Appointment.Status status) throws SQLException;
} 