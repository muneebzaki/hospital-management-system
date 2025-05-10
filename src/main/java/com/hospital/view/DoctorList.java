package com.hospital.view;

import com.hospital.controller.DoctorController;
import com.hospital.model.Doctor;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DoctorList extends JPanel {
    private final Dashboard dashboard;
    private final DoctorController doctorController;
    
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public DoctorList(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.doctorController = DoctorController.getInstance();
        
        setLayout(new BorderLayout());
        
        initComponents();
        loadDoctors();
    }
    
    private void initComponents() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Doctors");
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
        String[] columnNames = {"ID", "First Name", "Last Name", "Specialization", "Phone", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Doctor");
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
                showDoctorForm(null);
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = doctorTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer doctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    showDoctorForm(doctorId);
                } else {
                    JOptionPane.showMessageDialog(DoctorList.this, "Please select a doctor to edit.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = doctorTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer doctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(DoctorList.this, "Are you sure you want to delete this doctor?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = doctorController.deleteDoctor(doctorId);
                        if (success) {
                            loadDoctors();
                            JOptionPane.showMessageDialog(DoctorList.this, "Doctor deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(DoctorList.this, "Failed to delete doctor.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(DoctorList.this, "Please select a doctor to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = doctorTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer doctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    Doctor doctor = doctorController.getDoctorById(doctorId);
                    if (doctor != null) {
                        StringBuilder details = new StringBuilder();
                        details.append("Doctor Details\n\n");
                        details.append("ID: ").append(doctor.getId()).append("\n");
                        details.append("First Name: ").append(doctor.getFirstName()).append("\n");
                        details.append("Last Name: ").append(doctor.getLastName()).append("\n");
                        details.append("Specialization: ").append(doctor.getSpecialization()).append("\n");
                        details.append("Phone: ").append(doctor.getPhone()).append("\n");
                        details.append("Email: ").append(doctor.getEmail()).append("\n");
                        details.append("Address: ").append(doctor.getAddress());
                        
                        JTextArea textArea = new JTextArea(details.toString());
                        textArea.setEditable(false);
                        textArea.setWrapStyleWord(true);
                        textArea.setLineWrap(true);
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        scrollPane.setPreferredSize(new Dimension(400, 300));
                        
                        JOptionPane.showMessageDialog(DoctorList.this, scrollPane, "Doctor Details", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(DoctorList.this, "Please select a doctor to view.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                loadDoctors(searchTerm);
            }
        });
        
        // Double click on row
        doctorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = doctorTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Integer doctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
                        showDoctorForm(doctorId);
                    }
                }
            }
        });
        
        // Add components to panel
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Only admin can manage doctors
        User currentUser = dashboard.getCurrentUser();
        if (currentUser.getRole() != User.Role.ADMIN) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }
    
    private void loadDoctors() {
        loadDoctors("");
    }
    
    private void loadDoctors(String searchTerm) {
        // Clear table
        tableModel.setRowCount(0);
        
        List<Doctor> doctors = doctorController.getAllDoctors();
        
        for (Doctor doctor : doctors) {
            // If search term is provided, filter doctors
            if (!searchTerm.isEmpty()) {
                if (!doctor.getFirstName().toLowerCase().contains(searchTerm.toLowerCase()) &&
                    !doctor.getLastName().toLowerCase().contains(searchTerm.toLowerCase()) &&
                    !doctor.getSpecialization().toLowerCase().contains(searchTerm.toLowerCase()) &&
                    !doctor.getEmail().toLowerCase().contains(searchTerm.toLowerCase())) {
                    continue;
                }
            }
            
            Object[] row = {
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization(),
                doctor.getPhone(),
                doctor.getEmail()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showDoctorForm(Integer doctorId) {
        // Create a dialog for doctor form
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Doctor Form", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new BorderLayout());
        
        // Create form fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(new JLabel("First Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField firstNameField = new JTextField(20);
        fieldsPanel.add(firstNameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        fieldsPanel.add(new JLabel("Last Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField lastNameField = new JTextField(20);
        fieldsPanel.add(lastNameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        fieldsPanel.add(new JLabel("Specialization:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField specializationField = new JTextField(20);
        fieldsPanel.add(specializationField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        fieldsPanel.add(new JLabel("Phone:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField phoneField = new JTextField(20);
        fieldsPanel.add(phoneField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        fieldsPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField emailField = new JTextField(20);
        fieldsPanel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        fieldsPanel.add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        fieldsPanel.add(addressScrollPane, gbc);
        
        // If editing existing doctor, load their data
        Doctor doctor = null;
        if (doctorId != null) {
            doctor = doctorController.getDoctorById(doctorId);
            if (doctor != null) {
                firstNameField.setText(doctor.getFirstName());
                lastNameField.setText(doctor.getLastName());
                specializationField.setText(doctor.getSpecialization());
                phoneField.setText(doctor.getPhone());
                emailField.setText(doctor.getEmail());
                addressArea.setText(doctor.getAddress());
            }
        }
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        final Doctor finalDoctor = doctor;
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String specialization = specializationField.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressArea.getText().trim();
                
                // Validate inputs
                if (firstName.isEmpty() || lastName.isEmpty() || specialization.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Doctor doctorToSave;
                if (finalDoctor == null) {
                    // Create new doctor
                    doctorToSave = new Doctor();
                } else {
                    // Update existing doctor
                    doctorToSave = finalDoctor;
                }
                
                doctorToSave.setFirstName(firstName);
                doctorToSave.setLastName(lastName);
                doctorToSave.setSpecialization(specialization);
                doctorToSave.setPhone(phone);
                doctorToSave.setEmail(email);
                doctorToSave.setAddress(address);
                
                boolean success;
                if (finalDoctor == null) {
                    success = doctorController.createDoctor(doctorToSave);
                } else {
                    success = doctorController.updateDoctor(doctorToSave);
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Doctor saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadDoctors();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to save doctor.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        // Add panels to dialog
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(formPanel);
        dialog.setVisible(true);
    }
} 