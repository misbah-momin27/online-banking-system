/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banking;

import com.banking.ui.LoginFrame;
import com.banking.dao.AdminDAO;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // FIXED METHOD NAME
            
            // Ensure default admin exists
            System.out.println("Starting application...");
            AdminDAO adminDAO = new AdminDAO();
            boolean adminExists = adminDAO.createDefaultAdmin();
            System.out.println("Default admin ensured: " + adminExists);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
