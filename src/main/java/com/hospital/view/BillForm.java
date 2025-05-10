package com.hospital.view;

import com.hospital.controller.BillController;
import com.hospital.controller.PatientController;
import com.hospital.model.Bill;
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

public class BillForm extends JPanel {
    private final Dashboard dashboard;
    private final BillController billController;
    private final PatientController patientController;
    private Integer billId;
    
    private JComboBox<Patient> patientComboBox;
    private JTextField amountField;
    private JTextField dateField;
    private JTextArea descriptionArea;
    private JComboBox<Bill.Status> statusComboBox;
    
    public BillForm(Dashboard dashboard, Integer billId) {
        this.dashboard = dashboard;
        this.billId = billId;
        this.billController = BillController.getInstance();
        this.patientController = PatientController.getInstance();
        
        setLayout(new BorderLayout());
        
        initComponents();
        
        if (billId != null) {
            loadBill();
        }
    }
    
    private void initComponents() {
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel(billId == null ? "Generate New Bill" : "Edit Bill");
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
        
        // Amount
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Amount ($):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        amountField = new JTextField(10);
        formPanel.add(amountField, gbc);
        
        // Date
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        formPanel.add(dateField, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Status:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        statusComboBox = new JComboBox<>(Bill.Status.values());
        formPanel.add(statusComboBox, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descriptionScrollPane, gbc);
        
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
                saveBill();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard.showBillList();
            }
        });
        
        // Add panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadBill() {
        Bill bill = billController.getBillById(billId);
        if (bill != null) {
            for (int i = 0; i < patientComboBox.getItemCount(); i++) {
                Patient patient = patientComboBox.getItemAt(i);
                if (patient.getId().equals(bill.getPatientId())) {
                    patientComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            amountField.setText(String.valueOf(bill.getAmount()));
            dateField.setText(bill.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            descriptionArea.setText(bill.getDescription());
            statusComboBox.setSelectedItem(bill.getStatus());
        }
    }
    
    private void saveBill() {
        try {
            Patient selectedPatient = (Patient) patientComboBox.getSelectedItem();
            String amountText = amountField.getText().trim();
            String description = descriptionArea.getText().trim();
            Bill.Status status = (Bill.Status) statusComboBox.getSelectedItem();
            LocalDate date;
            double amount;
            
            try {
                date = LocalDate.parse(dateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (selectedPatient == null) {
                JOptionPane.showMessageDialog(this, "Please select a patient.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a description.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Bill bill;
            if (billId == null) {
                // Create new bill
                bill = new Bill();
                bill.setPatientId(selectedPatient.getId());
            } else {
                // Update existing bill
                bill = billController.getBillById(billId);
            }
            
            bill.setAmount(amount);
            bill.setDate(date);
            bill.setDescription(description);
            bill.setStatus(status);
            
            boolean success;
            if (billId == null) {
                success = billController.createBill(bill);
            } else {
                success = billController.updateBill(bill);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Bill saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dashboard.showBillList();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save bill.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
} 