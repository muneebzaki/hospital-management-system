package com.hospital.view;

import com.hospital.controller.BillController;
import com.hospital.controller.PatientController;
import com.hospital.model.Bill;
import com.hospital.model.Patient;
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

public class BillList extends JPanel {
    private final Dashboard dashboard;
    private final BillController billController;
    private final PatientController patientController;
    
    private JTable billTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public BillList(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.billController = BillController.getInstance();
        this.patientController = PatientController.getInstance();
        
        setLayout(new BorderLayout());
        
        initComponents();
        loadBills();
    }
    
    private void initComponents() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Bills");
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
        String[] columnNames = {"ID", "Patient", "Amount", "Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billTable = new JTable(tableModel);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(billTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Generate New Bill");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton viewButton = new JButton("View Details");
        JButton markPaidButton = new JButton("Mark as Paid");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(markPaidButton);
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard.showBillForm(null);
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = billTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer billId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    dashboard.showBillForm(billId);
                } else {
                    JOptionPane.showMessageDialog(BillList.this, "Please select a bill to edit.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = billTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer billId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(BillList.this, "Are you sure you want to delete this bill?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = billController.deleteBill(billId);
                        if (success) {
                            loadBills();
                            JOptionPane.showMessageDialog(BillList.this, "Bill deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(BillList.this, "Failed to delete bill.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(BillList.this, "Please select a bill to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = billTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer billId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    Bill bill = billController.getBillById(billId);
                    if (bill != null) {
                        Patient patient = patientController.getPatientById(bill.getPatientId());
                        
                        StringBuilder details = new StringBuilder();
                        details.append("Bill Details\n\n");
                        details.append("Patient: ").append(patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown").append("\n");
                        details.append("Amount: $").append(String.format("%.2f", bill.getAmount())).append("\n");
                        details.append("Date: ").append(bill.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n");
                        details.append("Status: ").append(bill.getStatus()).append("\n\n");
                        details.append("Description:\n").append(bill.getDescription());
                        
                        JTextArea textArea = new JTextArea(details.toString());
                        textArea.setEditable(false);
                        textArea.setWrapStyleWord(true);
                        textArea.setLineWrap(true);
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        scrollPane.setPreferredSize(new Dimension(400, 300));
                        
                        JOptionPane.showMessageDialog(BillList.this, scrollPane, "Bill Details", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(BillList.this, "Please select a bill to view.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        markPaidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = billTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer billId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    Bill bill = billController.getBillById(billId);
                    if (bill != null && bill.getStatus() != Bill.Status.PAID) {
                        bill.setStatus(Bill.Status.PAID);
                        boolean success = billController.updateBill(bill);
                        if (success) {
                            loadBills();
                            JOptionPane.showMessageDialog(BillList.this, "Bill marked as paid.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(BillList.this, "Failed to update bill status.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(BillList.this, "Bill is already marked as paid.", "Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(BillList.this, "Please select a bill to mark as paid.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                loadBills(searchTerm);
            }
        });
        
        // Double click on row
        billTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = billTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Integer billId = (Integer) tableModel.getValueAt(selectedRow, 0);
                        dashboard.showBillForm(billId);
                    }
                }
            }
        });
        
        // Add components to panel
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Disable add/edit/delete for non-admin users
        User currentUser = dashboard.getCurrentUser();
        if (currentUser.getRole() != User.Role.ADMIN) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            
            // Only admin and reception can mark as paid
            if (currentUser.getRole() != User.Role.NURSE) {
                markPaidButton.setEnabled(false);
            }
        }
    }
    
    private void loadBills() {
        loadBills("");
    }
    
    private void loadBills(String searchTerm) {
        // Clear table
        tableModel.setRowCount(0);
        
        List<Bill> bills = billController.getAllBills();
        
        for (Bill bill : bills) {
            Patient patient = patientController.getPatientById(bill.getPatientId());
            String patientName = patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown";
            
            // If search term is provided, filter bills
            if (!searchTerm.isEmpty()) {
                if (!patientName.toLowerCase().contains(searchTerm.toLowerCase()) &&
                    !bill.getStatus().toString().toLowerCase().contains(searchTerm.toLowerCase()) &&
                    !bill.getDescription().toLowerCase().contains(searchTerm.toLowerCase())) {
                    continue;
                }
            }
            
            Object[] row = {
                bill.getId(),
                patientName,
                String.format("$%.2f", bill.getAmount()),
                bill.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                bill.getStatus()
            };
            tableModel.addRow(row);
        }
    }
} 