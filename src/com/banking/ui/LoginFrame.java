package com.banking.ui;

import com.banking.dao.AdminDAO;
import com.banking.dao.UserDAO;
import com.banking.model.Admin;
import com.banking.model.User;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField accountField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeCombo;
    private JButton registerButton;
    private JPanel bottomPanel;
    
    public LoginFrame() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Online Banking System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("ONLINE BANKING SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        formPanel.add(new JLabel("User Type:"));
        userTypeCombo = new JComboBox<>(new String[]{"Customer", "Admin"});
        formPanel.add(userTypeCombo);
        
        formPanel.add(new JLabel("Account No/Username:"));
        accountField = new JTextField();
        formPanel.add(accountField);
        
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        
        formPanel.add(new JLabel()); // Empty cell
        JButton loginButton = new JButton("Login");
        formPanel.add(loginButton);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Register button panel
        bottomPanel = new JPanel(new FlowLayout());
        registerButton = new JButton("Register New Account");
        bottomPanel.add(registerButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Event listeners
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> openRegistration());
        
        // Add listener for user type combo box
        userTypeCombo.addActionListener(e -> toggleRegisterButtonVisibility());
        
        // Initialize visibility based on default selection
        toggleRegisterButtonVisibility();
    }
    
    private void toggleRegisterButtonVisibility() {
        String selectedUserType = (String) userTypeCombo.getSelectedItem();
        
        if ("Admin".equals(selectedUserType)) {
            // Hide register button for Admin
            registerButton.setVisible(false);
            bottomPanel.setVisible(false);
        } else {
            // Show register button for Customer
            registerButton.setVisible(true);
            bottomPanel.setVisible(true);
        }
        
        // Refresh the layout
        revalidate();
        repaint();
    }
    
    private void login() {
        String userType = (String) userTypeCombo.getSelectedItem();
        String account = accountField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (account.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }
        
        if ("Customer".equals(userType)) {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.loginUser(account, password);
            
            if (user != null) {
                new CustomerDashboard(user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid account number or password!");
            }
        } else {
            AdminDAO adminDAO = new AdminDAO();
            Admin admin = adminDAO.loginAdmin(account, password);
            
            if (admin != null) {
                new AdminDashboard().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials!");
            }
        }
    }
    
    private void openRegistration() {
        new RegistrationFrame().setVisible(true);
    }
}
