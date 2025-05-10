package com.hospital.view;

import com.hospital.controller.AppointmentController;
import com.hospital.controller.DoctorController;
import com.hospital.controller.PatientController;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AppointmentForm extends JPanel {
    private final Dashboard dashboard;
    private final Integer appointmentId;
    private final AppointmentController appointmentController;
    private final PatientController patientController;
    private final DoctorController doctorController;
    
    private JComboBox<ComboItem> cmbPatient;
    private JComboBox<ComboItem> cmbDoctor;
    private JTextField txtDate;
    private JTextField txtStartTime;
    private JTextField txtEndTime;
    private JTextArea txtNotes;
    private JComboBox<String> cmbStatus;
    private JButton btnSave;
    private JButton btnCancel;
    private JLabel lblStatus;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    public AppointmentForm(Dashboard dashboard, Integer appointmentId) {
        this.dashboard = dashboard;
        this.appointmentId = appointmentId;
        this.appointmentController = AppointmentController.getInstance();
        this.patientController = PatientController.getInstance();
        this.doctorController = DoctorController.getInstance();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        
        if (appointmentId != null) {
            loadAppointmentData();
        } else {
            // If user is a doctor, preselect the doctor
            User currentUser = dashboard.getCurrentUser();
            if (currentUser.getRole() == User.Role.DOCTOR) {
                Doctor currentDoctor = dashboard.getCurrentDoctor();
                if (currentDoctor != null) {
                    for (int i = 0; i < cmbDoctor.getItemCount(); i++) {
                        ComboItem item = cmbDoctor.getItemAt(i);
                        if (item.getId() == currentDoctor.getId()) {
                            cmbDoctor.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void initComponents() {
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Patient
        JLabel lblPatient = new JLabel("Patient:");
        cmbPatient = new JComboBox<>();
        loadPatients();
        formPanel.add(lblPatient);
        formPanel.add(cmbPatient);
        
        // Doctor
        JLabel lblDoctor = new JLabel("Doctor:");
        cmbDoctor = new JComboBox<>();
        loadDoctors();
        formPanel.add(lblDoctor);
        formPanel.add(cmbDoctor);
        
        // Date
        JLabel lblDate = new JLabel("Date (yyyy-MM-dd):");
        txtDate = new JTextField(10);
        formPanel.add(lblDate);
        formPanel.add(txtDate);
        
        // Start Time
        JLabel lblStartTime = new JLabel("Start Time (HH:mm):");
        txtStartTime = new JTextField(5);
        formPanel.add(lblStartTime);
        formPanel.add(txtStartTime);
        
        // End Time
        JLabel lblEndTime = new JLabel("End Time (HH:mm):");
        txtEndTime = new JTextField(5);
        formPanel.add(lblEndTime);
        formPanel.add(txtEndTime);
        
        // Status (only for editing)
        JLabel lblStatusField = new JLabel("Status:");
        String[] statuses = {"SCHEDULED", "COMPLETED", "CANCELLED"};
        cmbStatus = new JComboBox<>(statuses);
        formPanel.add(lblStatusField);
        formPanel.add(cmbStatus);
        
        // Notes
        JLabel lblNotes = new JLabel("Notes:");
        txtNotes = new JTextArea(3, 20);
        txtNotes.setLineWrap(true);
        JScrollPane notesScrollPane = new JScrollPane(txtNotes);
        formPanel.add(lblNotes);
        formPanel.add(notesScrollPane);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSave = new JButton(appointmentId == null ? "Book Appointment" : "Update Appointment");
        btnCancel = new JButton("Cancel");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        // Status label
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.RED);
        
        // Add action listeners
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAppointment();
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard.showAppointmentList();
            }
        });
        
        // Add components to main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(new JLabel(appointmentId == null ? "Book New Appointment" : "Edit Appointment", JLabel.CENTER), BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(lblStatus, BorderLayout.PAGE_END);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void loadPatients() {
        cmbPatient.removeAllItems();
        List<Patient> patients = patientController.getAllPatients();
        
        if (patients != null) {
            for (Patient patient : patients) {
                cmbPatient.addItem(new ComboItem(patient.getId(), patient.getName()));
            }
        }
    }
    
    private void loadDoctors() {
        cmbDoctor.removeAllItems();
        List<Doctor> doctors = doctorController.getAllDoctors();
        
        if (doctors != null) {
            for (Doctor doctor : doctors) {
                cmbDoctor.addItem(new ComboItem(doctor.getId(), doctor.getName()));
            }
        }
    }
    
    private void loadAppointmentData() {
        Appointment appointment = appointmentController.getAppointmentById(appointmentId);
        
        if (appointment != null) {
            // Set patient
            for (int i = 0; i < cmbPatient.getItemCount(); i++) {
                ComboItem item = cmbPatient.getItemAt(i);
                if (item.getId() == appointment.getPatientId()) {
                    cmbPatient.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set doctor
            for (int i = 0; i < cmbDoctor.getItemCount(); i++) {
                ComboItem item = cmbDoctor.getItemAt(i);
                if (item.getId() == appointment.getDoctorId()) {
                    cmbDoctor.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set date and times
            if (appointment.getAppointmentDate() != null) {
                txtDate.setText(dateFormat.format(appointment.getAppointmentDate()));
            }
            
            if (appointment.getStartTime() != null) {
                txtStartTime.setText(timeFormat.format(appointment.getStartTime()));
            }
            
            if (appointment.getEndTime() != null) {
                txtEndTime.setText(timeFormat.format(appointment.getEndTime()));
            }
            
            // Set status
            cmbStatus.setSelectedItem(appointment.getStatus().name());
            
            // Set notes
            txtNotes.setText(appointment.getNotes());
        } else {
            lblStatus.setText("Error: Appointment not found.");
        }
    }
    
    private void saveAppointment() {
        // Get selected patient and doctor
        ComboItem patientItem = (ComboItem) cmbPatient.getSelectedItem();
        ComboItem doctorItem = (ComboItem) cmbDoctor.getSelectedItem();
        
        if (patientItem == null || doctorItem == null) {
            lblStatus.setText("Error: Please select patient and doctor.");
            return;
        }
        
        int patientId = patientItem.getId();
        int doctorId = doctorItem.getId();
        
        // Parse date
        String dateStr = txtDate.getText().trim();
        if (dateStr.isEmpty()) {
            lblStatus.setText("Error: Date is required.");
            return;
        }
        
        Date appointmentDate;
        try {
            appointmentDate = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            lblStatus.setText("Error: Invalid date format. Use yyyy-MM-dd.");
            return;
        }
        
        // Parse times
        String startTimeStr = txtStartTime.getText().trim();
        String endTimeStr = txtEndTime.getText().trim();
        
        if (startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
            lblStatus.setText("Error: Start and end times are required.");
            return;
        }
        
        Time startTime;
        Time endTime;
        
        try {
            Date startDate = timeFormat.parse(startTimeStr);
            startTime = new Time(startDate.getTime());
            
            Date endDate = timeFormat.parse(endTimeStr);
            endTime = new Time(endDate.getTime());
            
            if (startTime.after(endTime) || startTime.equals(endTime)) {
                lblStatus.setText("Error: End time must be after start time.");
                return;
            }
        } catch (ParseException e) {
            lblStatus.setText("Error: Invalid time format. Use HH:mm.");
            return;
        }
        
        String notes = txtNotes.getText().trim();
        
        boolean success;
        
        if (appointmentId == null) {
            // Book new appointment
            success = appointmentController.bookAppointment(patientId, doctorId, appointmentDate, startTime, endTime, notes);
            
            if (success) {
                lblStatus.setText("Appointment booked successfully.");
                clearFields();
            } else {
                lblStatus.setText("Error: Failed to book appointment. Check for conflicts.");
            }
        } else {
            // Update existing appointment
            Appointment appointment = appointmentController.getAppointmentById(appointmentId);
            appointment.setPatientId(patientId);
            appointment.setDoctorId(doctorId);
            appointment.setAppointmentDate(appointmentDate);
            appointment.setStartTime(startTime);
            appointment.setEndTime(endTime);
            appointment.setStatus(Appointment.Status.valueOf((String) cmbStatus.getSelectedItem()));
            appointment.setNotes(notes);
            
            success = appointmentController.updateAppointment(appointment);
            
            if (success) {
                lblStatus.setText("Appointment updated successfully.");
            } else {
                lblStatus.setText("Error: Failed to update appointment. Check for conflicts.");
            }
        }
    }
    
    private void clearFields() {
        txtDate.setText("");
        txtStartTime.setText("");
        txtEndTime.setText("");
        txtNotes.setText("");
        cmbStatus.setSelectedIndex(0);
    }
    
    // Helper class for combo box items
    private static class ComboItem {
        private final int id;
        private final String name;
        
        public ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
} 