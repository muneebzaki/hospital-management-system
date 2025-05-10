package com.hospital.view;

import com.hospital.controller.AuthController;
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

public class UserList extends JPanel {
    private final Dashboard dashboard;
    private final AuthController authController;
    private final DoctorController doctorController;
    
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public UserList(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.authController = AuthController.getInstance();
        this.doctorController = DoctorController.getInstance();
        
        setLayout(new BorderLayout());
        
        initComponents();
        loadUsers();
    }
    
    private void initComponents() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Users");
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
        String[] columnNames = {"ID", "Username", "Role", "Email", "Active"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton resetPasswordButton = new JButton("Reset Password");
        JButton toggleActiveButton = new JButton("Toggle Active Status");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetPasswordButton);
        buttonPanel.add(toggleActiveButton);
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserForm(null);
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer userId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    showUserForm(userId);
                } else {
                    JOptionPane.showMessageDialog(UserList.this, "Please select a user to edit.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer userId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    
                    // Prevent deleting current user
                    if (userId.equals(dashboard.getCurrentUser().getId())) {
                        JOptionPane.showMessageDialog(UserList.this, "You cannot delete your own account.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    int confirm = JOptionPane.showConfirmDialog(UserList.this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = authController.deleteUser(userId);
                        if (success) {
                            loadUsers();
                            JOptionPane.showMessageDialog(UserList.this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(UserList.this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(UserList.this, "Please select a user to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        resetPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer userId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    String newPassword = JOptionPane.showInputDialog(UserList.this, "Enter new password:", "Reset Password", JOptionPane.PLAIN_MESSAGE);
                    
                    if (newPassword != null && !newPassword.isEmpty()) {
                        boolean success = authController.resetPassword(userId, newPassword);
                        if (success) {
                            JOptionPane.showMessageDialog(UserList.this, "Password reset successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(UserList.this, "Failed to reset password.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(UserList.this, "Please select a user to reset password.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        toggleActiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer userId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    
                    // Prevent deactivating current user
                    if (userId.equals(dashboard.getCurrentUser().getId())) {
                        JOptionPane.showMessageDialog(UserList.this, "You cannot deactivate your own account.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    boolean success = authController.toggleUserActive(userId);
                    if (success) {
                        loadUsers();
                        JOptionPane.showMessageDialog(UserList.this, "User status updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(UserList.this, "Failed to update user status.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(UserList.this, "Please select a user to toggle status.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                loadUsers(searchTerm);
            }
        });
        
        // Double click on row
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Integer userId = (Integer) tableModel.getValueAt(selectedRow, 0);
                        showUserForm(userId);
                    }
                }
            }
        });
        
        // Add components to panel
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadUsers() {
        loadUsers("");
    }
    
    private void loadUsers(String searchTerm) {
        // Clear table
        tableModel.setRowCount(0);
        
        List<User> users = authController.getAllUsers();
        
        for (User user : users) {
            // If search term is provided, filter users
            if (!searchTerm.isEmpty()) {
                if (!user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) &&
                    !user.getEmail().toLowerCase().contains(searchTerm.toLowerCase()) &&
                    !user.getRole().toString().toLowerCase().contains(searchTerm.toLowerCase())) {
                    continue;
                }
            }
            
            Object[] row = {
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getEmail(),
                user.isActive() ? "Yes" : "No"
            };
            tableModel.addRow(row);
        }
    }
    
    private void showUserForm(Integer userId) {
        // Create a dialog for user form
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "User Form", true);
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
        fieldsPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField usernameField = new JTextField(20);
        fieldsPanel.add(usernameField, gbc);
        
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
        fieldsPanel.add(new JLabel("Role:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JComboBox<User.Role> roleComboBox = new JComboBox<>(User.Role.values());
        fieldsPanel.add(roleComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        fieldsPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField passwordField = new JPasswordField(20);
        fieldsPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        fieldsPanel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField confirmPasswordField = new JPasswordField(20);
        fieldsPanel.add(confirmPasswordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        fieldsPanel.add(new JLabel("Active:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JCheckBox activeCheckBox = new JCheckBox();
        activeCheckBox.setSelected(true);
        fieldsPanel.add(activeCheckBox, gbc);
        
        // Doctor selection panel (only visible when role is DOCTOR)
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        JLabel doctorLabel = new JLabel("Link to Doctor:");
        fieldsPanel.add(doctorLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        List<Doctor> doctors = doctorController.getAllDoctors();
        JComboBox<Doctor> doctorComboBox = new JComboBox<>();
        doctorComboBox.addItem(null); // Add null option
        for (Doctor doctor : doctors) {
            doctorComboBox.addItem(doctor);
        }
        doctorComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Select Doctor --");
                } else if (value instanceof Doctor) {
                    Doctor doctor = (Doctor) value;
                    setText(doctor.getFirstName() + " " + doctor.getLastName());
                }
                return this;
            }
        });
        fieldsPanel.add(doctorComboBox, gbc);
        
        // Initially hide doctor selection
        doctorLabel.setVisible(false);
        doctorComboBox.setVisible(false);
        
        // Show/hide doctor selection based on role
        roleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User.Role selectedRole = (User.Role) roleComboBox.getSelectedItem();
                boolean isDoctorRole = selectedRole == User.Role.DOCTOR;
                doctorLabel.setVisible(isDoctorRole);
                doctorComboBox.setVisible(isDoctorRole);
            }
        });
        
        // If editing existing user, load their data
        User user = null;
        if (userId != null) {
            user = authController.getUserById(userId);
            if (user != null) {
                usernameField.setText(user.getUsername());
                emailField.setText(user.getEmail());
                roleComboBox.setSelectedItem(user.getRole());
                activeCheckBox.setSelected(user.isActive());
                
                // Password fields are empty for existing users
                passwordField.setEnabled(false);
                confirmPasswordField.setEnabled(false);
                
                // If user is a doctor, select the corresponding doctor
                if (user.getRole() == User.Role.DOCTOR) {
                    Doctor userDoctor = doctorController.getDoctorByUserId(userId);
                    if (userDoctor != null) {
                        for (int i = 0; i < doctorComboBox.getItemCount(); i++) {
                            Doctor doctor = doctorComboBox.getItemAt(i);
                            if (doctor != null && doctor.getId().equals(userDoctor.getId())) {
                                doctorComboBox.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                    
                    doctorLabel.setVisible(true);
                    doctorComboBox.setVisible(true);
                }
            }
        }
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        final User finalUser = user;
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                User.Role role = (User.Role) roleComboBox.getSelectedItem();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                boolean isActive = activeCheckBox.isSelected();
                Doctor selectedDoctor = (Doctor) doctorComboBox.getSelectedItem();
                
                // Validate inputs
                if (username.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (finalUser == null && (password.isEmpty() || !password.equals(confirmPassword))) {
                    JOptionPane.showMessageDialog(dialog, "Passwords do not match or are empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (role == User.Role.DOCTOR && selectedDoctor == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a doctor to link with this user account.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                User userToSave;
                boolean success;
                
                if (finalUser == null) {
                    // Create new user
                    success = authController.registerUser(username, password, email, role, isActive);
                    if (success && role == User.Role.DOCTOR && selectedDoctor != null) {
                        // Link user to doctor
                        User newUser = authController.getUserByUsername(username);
                        if (newUser != null) {
                            selectedDoctor.setUserId(newUser.getId());
                            doctorController.updateDoctor(selectedDoctor);
                        }
                    }
                } else {
                    // Update existing user
                    userToSave = finalUser;
                    userToSave.setUsername(username);
                    userToSave.setEmail(email);
                    userToSave.setRole(role);
                    userToSave.setActive(isActive);
                    
                    success = authController.updateUser(userToSave);
                    
                    // Update doctor link if needed
                    if (success && role == User.Role.DOCTOR && selectedDoctor != null) {
                        selectedDoctor.setUserId(userToSave.getId());
                        doctorController.updateDoctor(selectedDoctor);
                    }
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "User saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to save user.", "Error", JOptionPane.ERROR_MESSAGE);
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