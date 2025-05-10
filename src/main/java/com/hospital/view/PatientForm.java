package com.hospital.view;

import com.hospital.controller.PatientController;
import com.hospital.model.Patient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientForm extends JPanel {
    private final Dashboard dashboard;
    private final Integer patientId;
    private final PatientController patientController;
    
    private JTextField txtName;
    private JComboBox<String> cmbGender;
    private JTextField txtDateOfBirth;
    private JTextField txtBloodGroup;
    private JTextField txtContactNumber;
    private JTextArea txtAddress;
    private JButton btnSave;
    private JButton btnCancel;
    private JLabel lblStatus;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public PatientForm(Dashboard dashboard, Integer patientId) {
        this.dashboard = dashboard;
        this.patientId = patientId;
        this.patientController = PatientController.getInstance();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        
        if (patientId != null) {
            loadPatientData();
        }
    }
    
    private void initComponents() {
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Name
        JLabel lblName = new JLabel("Name:");
        txtName = new JTextField(20);
        formPanel.add(lblName);
        formPanel.add(txtName);
        
        // Gender
        JLabel lblGender = new JLabel("Gender:");
        String[] genders = {"MALE", "FEMALE", "OTHER"};
        cmbGender = new JComboBox<>(genders);
        formPanel.add(lblGender);
        formPanel.add(cmbGender);
        
        // Date of Birth
        JLabel lblDateOfBirth = new JLabel("Date of Birth (yyyy-MM-dd):");
        txtDateOfBirth = new JTextField(10);
        formPanel.add(lblDateOfBirth);
        formPanel.add(txtDateOfBirth);
        
        // Blood Group
        JLabel lblBloodGroup = new JLabel("Blood Group:");
        txtBloodGroup = new JTextField(5);
        formPanel.add(lblBloodGroup);
        formPanel.add(txtBloodGroup);
        
        // Contact Number
        JLabel lblContactNumber = new JLabel("Contact Number:");
        txtContactNumber = new JTextField(15);
        formPanel.add(lblContactNumber);
        formPanel.add(txtContactNumber);
        
        // Address
        JLabel lblAddress = new JLabel("Address:");
        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);
        JScrollPane addressScrollPane = new JScrollPane(txtAddress);
        formPanel.add(lblAddress);
        formPanel.add(addressScrollPane);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSave = new JButton(patientId == null ? "Add Patient" : "Update Patient");
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
                savePatient();
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard.showWelcomePanel();
            }
        });
        
        // Add components to main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(new JLabel(patientId == null ? "Add New Patient" : "Edit Patient", JLabel.CENTER), BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(lblStatus, BorderLayout.PAGE_END);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void loadPatientData() {
        Patient patient = patientController.getPatientById(patientId);
        
        if (patient != null) {
            txtName.setText(patient.getName());
            cmbGender.setSelectedItem(patient.getGender().name());
            
            if (patient.getDateOfBirth() != null) {
                txtDateOfBirth.setText(dateFormat.format(patient.getDateOfBirth()));
            }
            
            txtBloodGroup.setText(patient.getBloodGroup());
            txtContactNumber.setText(patient.getContactNumber());
            txtAddress.setText(patient.getAddress());
        } else {
            lblStatus.setText("Error: Patient not found.");
        }
    }
    
    private void savePatient() {
        String name = txtName.getText().trim();
        String genderStr = (String) cmbGender.getSelectedItem();
        String dobStr = txtDateOfBirth.getText().trim();
        String bloodGroup = txtBloodGroup.getText().trim();
        String contactNumber = txtContactNumber.getText().trim();
        String address = txtAddress.getText().trim();
        
        // Validation
        if (name.isEmpty()) {
            lblStatus.setText("Error: Name is required.");
            return;
        }
        
        if (contactNumber.isEmpty()) {
            lblStatus.setText("Error: Contact number is required.");
            return;
        }
        
        // Parse date of birth
        Date dob = null;
        if (!dobStr.isEmpty()) {
            try {
                dob = dateFormat.parse(dobStr);
            } catch (ParseException e) {
                lblStatus.setText("Error: Invalid date format. Use yyyy-MM-dd.");
                return;
            }
        }
        
        // Convert gender string to enum
        Patient.Gender gender = Patient.Gender.valueOf(genderStr);
        
        boolean success;
        
        if (patientId == null) {
            // Add new patient
            success = patientController.addPatient(name, gender, dob, bloodGroup, contactNumber, address);
            if (success) {
                lblStatus.setText("Patient added successfully.");
                clearFields();
            } else {
                lblStatus.setText("Error: Failed to add patient.");
            }
        } else {
            // Update existing patient
            Patient patient = patientController.getPatientById(patientId);
            patient.setName(name);
            patient.setGender(gender);
            patient.setDateOfBirth(dob);
            patient.setBloodGroup(bloodGroup);
            patient.setContactNumber(contactNumber);
            patient.setAddress(address);
            
            success = patientController.updatePatient(patient);
            
            if (success) {
                lblStatus.setText("Patient updated successfully.");
            } else {
                lblStatus.setText("Error: Failed to update patient.");
            }
        }
    }
    
    private void clearFields() {
        txtName.setText("");
        cmbGender.setSelectedIndex(0);
        txtDateOfBirth.setText("");
        txtBloodGroup.setText("");
        txtContactNumber.setText("");
        txtAddress.setText("");
    }
} 