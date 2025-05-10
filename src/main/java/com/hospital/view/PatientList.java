package com.hospital.view;

import com.hospital.controller.PatientController;
import com.hospital.model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class PatientList extends JPanel {
    private final Dashboard dashboard;
    private final PatientController patientController;
    
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public PatientList(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.patientController = PatientController.getInstance();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        loadPatients();
    }
    
    private void initComponents() {
        // Create table model with columns
        String[] columns = {"ID", "Name", "Gender", "Date of Birth", "Blood Group", "Contact Number"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Create table and scroll pane
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSearch = new JLabel("Search by name: ");
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Search");
        btnRefresh = new JButton("Refresh");
        
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Add New Patient");
        btnEdit = new JButton("Edit Selected");
        btnDelete = new JButton("Delete Selected");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        
        // Add action listeners
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPatients();
            }
        });
        
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                loadPatients();
            }
        });
        
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard.showPatientForm(null);
            }
        });
        
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedPatient();
            }
        });
        
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedPatient();
            }
        });
        
        // Add components to panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Patient List", JLabel.CENTER), BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadPatients() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all patients
        List<Patient> patients = patientController.getAllPatients();
        
        if (patients != null) {
            for (Patient patient : patients) {
                Object[] rowData = {
                    patient.getId(),
                    patient.getName(),
                    patient.getGender(),
                    patient.getDateOfBirth() != null ? dateFormat.format(patient.getDateOfBirth()) : "",
                    patient.getBloodGroup(),
                    patient.getContactNumber()
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    private void searchPatients() {
        String searchTerm = txtSearch.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadPatients();
            return;
        }
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Search patients by name
        List<Patient> patients = patientController.searchPatientsByName(searchTerm);
        
        if (patients != null) {
            for (Patient patient : patients) {
                Object[] rowData = {
                    patient.getId(),
                    patient.getName(),
                    patient.getGender(),
                    patient.getDateOfBirth() != null ? dateFormat.format(patient.getDateOfBirth()) : "",
                    patient.getBloodGroup(),
                    patient.getContactNumber()
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    private void editSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        dashboard.showPatientForm(patientId);
    }
    
    private void deleteSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete patient: " + patientName + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = patientController.deletePatient(patientId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Patient deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPatients();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete patient.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 