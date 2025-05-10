package com.hospital.view;

import com.hospital.controller.AuthController;
import com.hospital.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblStatus;
    
    private final AuthController authController;
    
    public LoginForm() {
        authController = AuthController.getInstance();
        
        setTitle("Hospital Management System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
    }
    
    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        
        JLabel lblUsername = new JLabel("Username:");
        txtUsername = new JTextField(20);
        
        JLabel lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField(20);
        
        formPanel.add(lblUsername);
        formPanel.add(txtUsername);
        formPanel.add(lblPassword);
        formPanel.add(txtPassword);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnLogin = new JButton("Login");
        btnExit = new JButton("Exit");
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);
        
        // Status panel
        JPanel statusPanel = new JPanel();
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.RED);
        statusPanel.add(lblStatus);
        
        // Add panels to main panel
        mainPanel.add(new JLabel("Welcome to Hospital Management System", JLabel.CENTER), BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(statusPanel, BorderLayout.PAGE_END);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add action listeners
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Set default button
        getRootPane().setDefaultButton(btnLogin);
    }
    
    private void login() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please enter username and password");
            return;
        }
        
        boolean success = authController.login(username, password);
        
        if (success) {
            User currentUser = authController.getCurrentUser();
            openDashboard(currentUser);
        } else {
            lblStatus.setText("Invalid username or password");
            txtPassword.setText("");
        }
    }
    
    private void openDashboard(User user) {
        Dashboard dashboard = new Dashboard(user);
        dashboard.setVisible(true);
        this.dispose();
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
} 