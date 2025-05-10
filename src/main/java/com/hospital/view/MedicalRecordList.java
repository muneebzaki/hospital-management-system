package com.hospital.view;

import com.hospital.controller.MedicalRecordController;
import com.hospital.controller.PatientController;
import com.hospital.controller.DoctorController;
import com.hospital.model.MedicalRecord;
import com.hospital.model.Patient;
import com.hospital.model.Doctor;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MedicalRecordList extends JPanel {
    private final Dashboard dashboard;
    private final MedicalRecordController medicalRecordController;
    private final PatientController patientController;
    private final DoctorController doctorController;
    
    private JTable recordTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public MedicalRecordList(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.medicalRecordController = MedicalRecordController.getInstance();
        this.patientController = PatientController.getInstance();
        this.doctorController = DoctorController.getInstance();
        
        setLayout(new BorderLayout());
        
        initComponents();
        loadMedicalRecords();
    }
    
    private void initComponents() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Medical Records");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        titlePanel.add(searchPanel, BorderLayout.EAST);
        
        // Table panel
        String[] columnNames = {"ID", "Patient", "Doctor", "Date", "Diagnosis"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recordTable = new JTable(tableModel);
        recordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(recordTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Medical Record");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton viewButton = new JButton("View Details");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard.showMedicalRecordForm(null);
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = recordTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer recordId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    dashboard.showMedicalRecordForm(recordId);
                } else {
                    JOptionPane.showMessageDialog(MedicalRecordList.this, "Please select a record to edit.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = recordTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer recordId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(MedicalRecordList.this, "Are you sure you want to delete this medical record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = medicalRecordController.deleteMedicalRecord(recordId);
                        if (success) {
                            loadMedicalRecords();
                            JOptionPane.showMessageDialog(MedicalRecordList.this, "Medical record deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(MedicalRecordList.this, "Failed to delete medical record.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(MedicalRecordList.this, "Please select a record to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = recordTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer recordId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    MedicalRecord record = medicalRecordController.getMedicalRecordById(recordId);
                    if (record != null) {
                        Patient patient = patientController.getPatientById(record.getPatientId());
                        Doctor doctor = record.getDoctorId() != null ? doctorController.getDoctorById(record.getDoctorId()) : null;
                        
                        StringBuilder details = new StringBuilder();
                        details.append("Medical Record Details\n\n");
                        details.append("Patient: ").append(patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown").append("\n");
                        details.append("Doctor: ").append(doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "Unknown").append("\n");
                        details.append("Date: ").append(record.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n");
                        details.append("Diagnosis: ").append(record.getDiagnosis()).append("\n\n");
                        details.append("Treatment:\n").append(record.getTreatment()).append("\n\n");
                        details.append("Notes:\n").append(record.getNotes());
                        
                        JTextArea textArea = new JTextArea(details.toString());
                        textArea.setEditable(false);
                        textArea.setWrapStyleWord(true);
                        textArea.setLineWrap(true);
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        scrollPane.setPreferredSize(new Dimension(400, 300));
                        
                        JOptionPane.showMessageDialog(MedicalRecordList.this, scrollPane, "Medical Record Details", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(MedicalRecordList.this, "Please select a record to view.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                loadMedicalRecords(searchTerm);
            }
        });
        
        // Double click on row
        recordTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = recordTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Integer recordId = (Integer) tableModel.getValueAt(selectedRow, 0);
                        dashboard.showMedicalRecordForm(recordId);
                    }
                }
            }
        });
        
        // Add components to panel
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Disable add/edit/delete for non-doctors and non-admins
        User currentUser = dashboard.getCurrentUser();
        if (currentUser.getRole() != User.Role.DOCTOR && currentUser.getRole() != User.Role.ADMIN) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }
    
    private void loadMedicalRecords() {
        loadMedicalRecords("");
    }
    
    private void loadMedicalRecords(String searchTerm) {
        // Clear table
        tableModel.setRowCount(0);
        
        List<MedicalRecord> records;
        User currentUser = dashboard.getCurrentUser();
        
        if (currentUser.getRole() == User.Role.DOCTOR && dashboard.getCurrentDoctor() != null) {
            // If doctor is logged in, only show their records
            records = medicalRecordController.getMedicalRecordsByDoctorId(dashboard.getCurrentDoctor().getId());
        } else {
            // Otherwise show all records
            records = medicalRecordController.getAllMedicalRecords();
        }
        
        for (MedicalRecord record : records) {
            Patient patient = patientController.getPatientById(record.getPatientId());
            Doctor doctor = record.getDoctorId() != null ? doctorController.getDoctorById(record.getDoctorId()) : null;
            
            String patientName = patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown";
            String doctorName = doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "Unknown";
            
            // If search term is provided, filter records
            if (!searchTerm.isEmpty()) {
                if (!patientName.toLowerCase().contains(searchTerm.toLowerCase()) &&
                    !doctorName.toLowerCase().contains(searchTerm.toLowerCase()) &&
                    !record.getDiagnosis().toLowerCase().contains(searchTerm.toLowerCase())) {
                    continue;
                }
            }
            
            Object[] row = {
                record.getId(),
                patientName,
                doctorName,
                record.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                record.getDiagnosis()
            };
            tableModel.addRow(row);
        }
    }
} 