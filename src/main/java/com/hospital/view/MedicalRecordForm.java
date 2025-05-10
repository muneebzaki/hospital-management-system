package com.hospital.view;

import com.hospital.controller.MedicalRecordController;
import com.hospital.controller.PatientController;
import com.hospital.model.MedicalRecord;
import com.hospital.model.Patient;
import com.hospital.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MedicalRecordForm extends JPanel {
    private final Dashboard dashboard;
    private final MedicalRecordController medicalRecordController;
    private final PatientController patientController;
    private Integer recordId;
    
    private JComboBox<Patient> patientComboBox;
    private JTextField diagnosisField;
    private JTextArea treatmentArea;
    private JTextField dateField;
    private JTextArea notesArea;
    
    public MedicalRecordForm(Dashboard dashboard, Integer recordId) {
        this.dashboard = dashboard;
        this.recordId = recordId;
        this.medicalRecordController = MedicalRecordController.getInstance();
        this.patientController = PatientController.getInstance();
        
        setLayout(new BorderLayout());
        
        initComponents();
        
        if (recordId != null) {
            loadMedicalRecord();
        }
    }
    
    private void initComponents() {
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel(recordId == null ? "Add Medical Record" : "Edit Medical Record");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Patient selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Patient:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        List<Patient> patients = patientController.getAllPatients();
        patientComboBox = new JComboBox<>(patients.toArray(new Patient[0]));
        patientComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patient) {
                    Patient patient = (Patient) value;
                    setText(patient.getFirstName() + " " + patient.getLastName());
                }
                return this;
            }
        });
        formPanel.add(patientComboBox, gbc);
        
        // Date
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        formPanel.add(dateField, gbc);
        
        // Diagnosis
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Diagnosis:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        diagnosisField = new JTextField(20);
        formPanel.add(diagnosisField, gbc);
        
        // Treatment
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Treatment:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        treatmentArea = new JTextArea(5, 20);
        treatmentArea.setLineWrap(true);
        treatmentArea.setWrapStyleWord(true);
        JScrollPane treatmentScrollPane = new JScrollPane(treatmentArea);
        formPanel.add(treatmentScrollPane, gbc);
        
        // Notes
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Notes:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        notesArea = new JTextArea(5, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        formPanel.add(notesScrollPane, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMedicalRecord();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard.showAppointmentList();
            }
        });
        
        // Add panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Disable patient selection if doctor is logged in
        User currentUser = dashboard.getCurrentUser();
        if (currentUser.getRole() == User.Role.DOCTOR) {
            patientComboBox.setEnabled(false);
        }
    }
    
    private void loadMedicalRecord() {
        MedicalRecord record = medicalRecordController.getMedicalRecordById(recordId);
        if (record != null) {
            for (int i = 0; i < patientComboBox.getItemCount(); i++) {
                Patient patient = patientComboBox.getItemAt(i);
                if (patient.getId().equals(record.getPatientId())) {
                    patientComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            diagnosisField.setText(record.getDiagnosis());
            treatmentArea.setText(record.getTreatment());
            dateField.setText(record.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            notesArea.setText(record.getNotes());
        }
    }
    
    private void saveMedicalRecord() {
        try {
            Patient selectedPatient = (Patient) patientComboBox.getSelectedItem();
            String diagnosis = diagnosisField.getText().trim();
            String treatment = treatmentArea.getText().trim();
            String notes = notesArea.getText().trim();
            LocalDate date;
            
            try {
                date = LocalDate.parse(dateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (selectedPatient == null) {
                JOptionPane.showMessageDialog(this, "Please select a patient.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (diagnosis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a diagnosis.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (treatment.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a treatment.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            MedicalRecord record;
            if (recordId == null) {
                // Create new record
                record = new MedicalRecord();
                record.setPatientId(selectedPatient.getId());
                record.setDoctorId(dashboard.getCurrentDoctor() != null ? dashboard.getCurrentDoctor().getId() : null);
            } else {
                // Update existing record
                record = medicalRecordController.getMedicalRecordById(recordId);
            }
            
            record.setDiagnosis(diagnosis);
            record.setTreatment(treatment);
            record.setDate(date);
            record.setNotes(notes);
            
            boolean success;
            if (recordId == null) {
                success = medicalRecordController.createMedicalRecord(record);
            } else {
                success = medicalRecordController.updateMedicalRecord(record);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Medical record saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dashboard.showAppointmentList();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save medical record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
} 