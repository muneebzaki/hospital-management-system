package com.hospital;

import com.hospital.view.LoginForm;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel to system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Set font for all Swing components
        setUIFont(new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 12));
        
        // Start the application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }
    
    // Helper method to set font for all Swing components
    private static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
} 