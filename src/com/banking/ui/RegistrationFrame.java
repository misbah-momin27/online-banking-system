/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banking.ui;

import com.banking.dao.UserDAO;
import com.banking.model.User;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class RegistrationFrame extends JFrame {
    private JTextField nameField, emailField, accountField;
    private JPasswordField passwordField, confirmPasswordField;
    
    public RegistrationFrame() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Register New Account");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        mainPanel.add(nameField);
        
        mainPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        mainPanel.add(emailField);
        
        mainPanel.add(new JLabel("Account Number:"));
        accountField = new JTextField();
        mainPanel.add(accountField);
        
        mainPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        mainPanel.add(passwordField);
        
        mainPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        mainPanel.add(confirmPasswordField);
        
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");
        
        mainPanel.add(registerButton);
        mainPanel.add(cancelButton);
        
        add(mainPanel);
        
        registerButton.addActionListener(e -> register());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String accountNo = accountField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (name.isEmpty() || email.isEmpty() || accountNo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }
        
        if (accountNo.length() < 4) {
            JOptionPane.showMessageDialog(this, "Account number must be at least 4 characters!");
            return;
        }
        
        User user = new User(accountNo, name, email, password, new BigDecimal("0.00"));
        UserDAO userDAO = new UserDAO();
        
        if (userDAO.registerUser(user)) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed! Account number or email may already exist.");
        }
    }
}
