package com.hospital.view;

import com.hospital.controller.AppointmentController;
import com.hospital.controller.DoctorController;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class AppointmentList extends JPanel {
    private final Dashboard dashboard;
    private final AppointmentController appointmentController;
    private final User currentUser;
    private final Doctor currentDoctor;
    
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnCancel;
    private JButton btnComplete;
    private JButton btnDelete;
    private JButton btnRefresh;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    public AppointmentList(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.appointmentController = AppointmentController.getInstance();
        this.currentUser = dashboard.getCurrentUser();
        this.currentDoctor = dashboard.getCurrentDoctor();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        loadAppointments();
    }
    
    private void initComponents() {
        // Create table model with columns
        String[] columns = {"ID", "Patient", "Doctor", "Date", "Start Time", "End Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Create table and scroll pane
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Book New Appointment");
        btnEdit = new JButton("Edit Selected");
        btnCancel = new JButton("Cancel Appointment");
        btnComplete = new JButton("Mark as Completed");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnComplete);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        
        // Add action listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard.showAppointmentForm(null);
            }
        });
        
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedAppointment();
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelSelectedAppointment();
            }
        });
        
        btnComplete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completeSelectedAppointment();
            }
        });
        
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedAppointment();
            }
        });
        
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAppointments();
            }
        });
        
        // Add components to panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Appointment List", JLabel.CENTER), BorderLayout.NORTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadAppointments() {
        // Clear table
        tableModel.setRowCount(0);
        
        List<Appointment> appointments;
        
        // If user is a doctor, only show their appointments
        if (currentUser.getRole() == User.Role.DOCTOR && currentDoctor != null) {
            appointments = appointmentController.getAppointmentsByDoctorId(currentDoctor.getId());
        } else {
            appointments = appointmentController.getAllAppointments();
        }
        
        if (appointments != null) {
            for (Appointment appointment : appointments) {
                Object[] rowData = {
                    appointment.getId(),
                    appointment.getPatientName(),
                    appointment.getDoctorName(),
                    appointment.getAppointmentDate() != null ? dateFormat.format(appointment.getAppointmentDate()) : "",
                    appointment.getStartTime() != null ? timeFormat.format(appointment.getStartTime()) : "",
                    appointment.getEndTime() != null ? timeFormat.format(appointment.getEndTime()) : "",
                    appointment.getStatus()
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    private void editSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        dashboard.showAppointmentForm(appointmentId);
    }
    
    private void cancelSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 6);
        
        if (status.equals(Appointment.Status.CANCELLED.name())) {
            JOptionPane.showMessageDialog(this, "This appointment is already cancelled.", "Already Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (status.equals(Appointment.Status.COMPLETED.name())) {
            JOptionPane.showMessageDialog(this, "Cannot cancel a completed appointment.", "Already Completed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this appointment?", 
            "Confirm Cancel", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = appointmentController.cancelAppointment(appointmentId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void completeSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to mark as completed.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 6);
        
        if (status.equals(Appointment.Status.COMPLETED.name())) {
            JOptionPane.showMessageDialog(this, "This appointment is already completed.", "Already Completed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (status.equals(Appointment.Status.CANCELLED.name())) {
            JOptionPane.showMessageDialog(this, "Cannot complete a cancelled appointment.", "Cancelled", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to mark this appointment as completed?", 
            "Confirm Complete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = appointmentController.completeAppointment(appointmentId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Appointment marked as completed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update appointment status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        String doctorName = (String) tableModel.getValueAt(selectedRow, 2);
        String date = (String) tableModel.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the appointment for patient: " + patientName + 
            " with doctor: " + doctorName + " on " + date + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = appointmentController.deleteAppointment(appointmentId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Appointment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 