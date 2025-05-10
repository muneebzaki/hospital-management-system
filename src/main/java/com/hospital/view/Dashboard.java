package com.hospital.view;

import com.hospital.controller.AuthController;
import com.hospital.controller.DoctorController;
import com.hospital.model.Doctor;
import com.hospital.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JMenuBar menuBar;
    
    private final User currentUser;
    private final AuthController authController;
    private Doctor currentDoctor;
    
    public Dashboard(User user) {
        this.currentUser = user;
        this.authController = AuthController.getInstance();
        
        // If user is a doctor, get the doctor details
        if (user.getRole() == User.Role.DOCTOR) {
            DoctorController doctorController = DoctorController.getInstance();
            currentDoctor = doctorController.getDoctorByUserId(user.getId());
        }
        
        setTitle("Hospital Management System - Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        setupMenuBar();
        
        // Show welcome panel by default
        showWelcomePanel();
    }
    
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        
        // Create content panel where different views will be displayed
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        statusBar.add(statusLabel);
        
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Patients menu
        JMenu patientMenu = new JMenu("Patients");
        JMenuItem addPatientItem = new JMenuItem("Add Patient");
        JMenuItem viewPatientsItem = new JMenuItem("View Patients");
        
        patientMenu.add(addPatientItem);
        patientMenu.add(viewPatientsItem);
        
        // Appointments menu
        JMenu appointmentMenu = new JMenu("Appointments");
        JMenuItem bookAppointmentItem = new JMenuItem("Book Appointment");
        JMenuItem viewAppointmentsItem = new JMenuItem("View Appointments");
        
        appointmentMenu.add(bookAppointmentItem);
        appointmentMenu.add(viewAppointmentsItem);
        
        // Medical Records menu
        JMenu medicalRecordMenu = new JMenu("Medical Records");
        JMenuItem addMedicalRecordItem = new JMenuItem("Add Medical Record");
        JMenuItem viewMedicalRecordsItem = new JMenuItem("View Medical Records");
        
        medicalRecordMenu.add(addMedicalRecordItem);
        medicalRecordMenu.add(viewMedicalRecordsItem);
        
        // Billing menu
        JMenu billingMenu = new JMenu("Billing");
        JMenuItem generateBillItem = new JMenuItem("Generate Bill");
        JMenuItem viewBillsItem = new JMenuItem("View Bills");
        
        billingMenu.add(generateBillItem);
        billingMenu.add(viewBillsItem);
        
        // Admin menu (only visible to admin)
        JMenu adminMenu = new JMenu("Administration");
        JMenuItem manageDoctorsItem = new JMenuItem("Manage Doctors");
        JMenuItem manageUsersItem = new JMenuItem("Manage Users");
        
        adminMenu.add(manageDoctorsItem);
        adminMenu.add(manageUsersItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(patientMenu);
        menuBar.add(appointmentMenu);
        menuBar.add(medicalRecordMenu);
        menuBar.add(billingMenu);
        
        // Only add admin menu for admin users
        if (authController.isAdmin()) {
            menuBar.add(adminMenu);
        }
        
        // Set menu bar to frame
        setJMenuBar(menuBar);
        
        // Add action listeners
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        addPatientItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPatientForm(null);
            }
        });
        
        viewPatientsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPatientList();
            }
        });
        
        bookAppointmentItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAppointmentForm(null);
            }
        });
        
        viewAppointmentsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAppointmentList();
            }
        });
        
        addMedicalRecordItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMedicalRecordForm(null);
            }
        });
        
        viewMedicalRecordsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMedicalRecordList();
            }
        });
        
        generateBillItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBillForm(null);
            }
        });
        
        viewBillsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBillList();
            }
        });
        
        manageDoctorsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDoctorList();
            }
        });
        
        manageUsersItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserList();
            }
        });
    }
    
    void showWelcomePanel() {
        contentPanel.removeAll();
        
        JPanel welcomePanel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Welcome to Hospital Management System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JLabel userLabel = new JLabel("User: " + currentUser.getUsername());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        
        JLabel roleLabel = new JLabel("Role: " + currentUser.getRole());
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        
        JLabel instructionLabel = new JLabel("Please select an option from the menu above.");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        
        infoPanel.add(userLabel);
        infoPanel.add(roleLabel);
        infoPanel.add(instructionLabel);
        
        welcomePanel.add(infoPanel, BorderLayout.CENTER);
        
        contentPanel.add(welcomePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    void showPatientForm(Integer patientId) {
        contentPanel.removeAll();
        PatientForm patientForm = new PatientForm(this, patientId);
        contentPanel.add(patientForm);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showPatientList() {
        contentPanel.removeAll();
        PatientList patientList = new PatientList(this);
        contentPanel.add(patientList);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    void showAppointmentForm(Integer appointmentId) {
        contentPanel.removeAll();
        AppointmentForm appointmentForm = new AppointmentForm(this, appointmentId);
        contentPanel.add(appointmentForm);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    public void showAppointmentList() {
        contentPanel.removeAll();
        AppointmentList appointmentList = new AppointmentList(this);
        contentPanel.add(appointmentList);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showMedicalRecordForm(Integer recordId) {
        contentPanel.removeAll();
        MedicalRecordForm medicalRecordForm = new MedicalRecordForm(this, recordId);
        contentPanel.add(medicalRecordForm);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showMedicalRecordList() {
        contentPanel.removeAll();
        MedicalRecordList medicalRecordList = new MedicalRecordList(this);
        contentPanel.add(medicalRecordList);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showBillForm(Integer billId) {
        contentPanel.removeAll();
        BillForm billForm = new BillForm(this, billId);
        contentPanel.add(billForm);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showBillList() {
        contentPanel.removeAll();
        BillList billList = new BillList(this);
        contentPanel.add(billList);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showDoctorList() {
        contentPanel.removeAll();
        DoctorList doctorList = new DoctorList(this);
        contentPanel.add(doctorList);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showUserList() {
        contentPanel.removeAll();
        UserList userList = new UserList(this);
        contentPanel.add(userList);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void logout() {
        authController.logout();
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
        this.dispose();
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public Doctor getCurrentDoctor() {
        return currentDoctor;
    }
} 