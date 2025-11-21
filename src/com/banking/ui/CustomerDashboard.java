/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banking.ui;

import com.banking.model.User;
import com.banking.service.BankingService;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CustomerDashboard extends JFrame {
    private final User user;
    private final BankingService bankingService;
    private final String accountType;
    private final NumberFormat rupeeFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    
    public CustomerDashboard(User user) {
        this.user = user;
        this.bankingService = new BankingService();
        this.accountType = determineAccountType();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Customer Dashboard - " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        
        // Account Menu (for balance viewing)
        JMenu accountMenu = new JMenu("Account");
        JMenuItem viewBalanceItem = new JMenuItem("View Balance");
        JMenuItem accountDetailsItem = new JMenuItem("Account Details");
        accountMenu.add(viewBalanceItem);
        accountMenu.add(accountDetailsItem);
        
        // Transactions Menu
        JMenu transactionsMenu = new JMenu("Transactions");
        JMenuItem depositItem = new JMenuItem("Deposit");
        JMenuItem withdrawItem = new JMenuItem("Withdraw");
        JMenuItem transferItem = new JMenuItem("Transfer");
        transactionsMenu.add(depositItem);
        transactionsMenu.add(withdrawItem);
        transactionsMenu.add(transferItem);
        
        // Logout Menu
        JMenuItem logoutItem = new JMenuItem("Logout");
        
        menuBar.add(accountMenu);
        menuBar.add(transactionsMenu);
        menuBar.add(logoutItem);
        setJMenuBar(menuBar);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Account Information Panel
        JPanel accountInfoPanel = createAccountInfoPanel();
        mainPanel.add(accountInfoPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event listeners
        viewBalanceItem.addActionListener(e -> showBalanceDialog());
        accountDetailsItem.addActionListener(e -> showAccountDetailsDialog());
        depositItem.addActionListener(e -> showDepositDialog());
        withdrawItem.addActionListener(e -> showWithdrawDialog());
        transferItem.addActionListener(e -> showTransferDialog());
        logoutItem.addActionListener(e -> logout());
    }
    
    private JPanel createAccountInfoPanel() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Account Details"));
        infoPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Account Type
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Account Type:"), gbc);
        gbc.gridx = 1;
        JLabel typeLabel = new JLabel(accountType);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        typeLabel.setForeground(getAccountTypeColor());
        infoPanel.add(typeLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        JLabel accNoLabel = new JLabel(user.getAccountNo());
        accNoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(accNoLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Account Holder:"), gbc);
        gbc.gridx = 1;
        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(nameLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(user.getEmail()), gbc);
        
        return infoPanel;
    }
    
    private String determineAccountType() {
        // You can modify this logic based on your actual data structure
        // For now, using a simple logic - you might want to store account type in User model
        if (user.getBalance().compareTo(new BigDecimal("100000")) >= 0) {
            return "Current Account";
        } else {
            return "Savings Account";
        }
    }
    
    private Color getAccountTypeColor() {
        return accountType.equals("Current Account") ? new Color(0, 100, 0) : Color.BLUE;
    }
    
    private void showBalanceDialog() {
        BigDecimal currentBalance = bankingService.getBalance(user.getAccountNo());
        if (currentBalance != null) {
            user.setBalance(currentBalance);
            JOptionPane.showMessageDialog(this, 
                "<html><b>Account Balance Details:</b><br><br>" +
                "Current Balance: <font color='green'><b>" + rupeeFormat.format(currentBalance) + "</b></font><br>" +
                "Account Type: " + accountType + "<br>" +
                "Account Number: " + user.getAccountNo() + "</html>",
                "Account Balance",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Unable to retrieve balance. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAccountDetailsDialog() {
        BigDecimal currentBalance = bankingService.getBalance(user.getAccountNo());
        JOptionPane.showMessageDialog(this,
            "<html><b>Account Details:</b><br><br>" +
            "Account Number: <b>" + user.getAccountNo() + "</b><br>" +
            "Account Holder: <b>" + user.getName() + "</b><br>" +
            "Account Type: <b>" + accountType + "</b><br>" +
            "Current Balance: <font color='green'><b>" + rupeeFormat.format(currentBalance) + "</b></font><br>" +
            "Email: " + user.getEmail() + "</html>",
            "Account Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showDepositDialog() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter deposit amount (₹):");
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                BigDecimal amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    if (bankingService.deposit(user.getAccountNo(), amount)) {
                        // Update user balance
                        BigDecimal newBalance = bankingService.getBalance(user.getAccountNo());
                        user.setBalance(newBalance);
                        JOptionPane.showMessageDialog(this, 
                            "<html><b>Deposit Successful!</b><br><br>" +
                            "Amount Deposited: <font color='green'><b>" + rupeeFormat.format(amount) + "</b></font><br>" +
                            "New Balance: <font color='blue'><b>" + rupeeFormat.format(newBalance) + "</b></font></html>");
                    } else {
                        JOptionPane.showMessageDialog(this, "Deposit failed! Please try again.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format! Please enter a valid number.");
            }
        }
    }
    
    private void showWithdrawDialog() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount (₹):");
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                BigDecimal amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    if (bankingService.withdraw(user.getAccountNo(), amount)) {
                        // Update user balance
                        BigDecimal newBalance = bankingService.getBalance(user.getAccountNo());
                        user.setBalance(newBalance);
                        JOptionPane.showMessageDialog(this, 
                            "<html><b>Withdrawal Successful!</b><br><br>" +
                            "Amount Withdrawn: <font color='red'><b>" + rupeeFormat.format(amount) + "</b></font><br>" +
                            "New Balance: <font color='blue'><b>" + rupeeFormat.format(newBalance) + "</b></font></html>");
                    } else {
                        JOptionPane.showMessageDialog(this, "Withdrawal failed! Insufficient balance.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format! Please enter a valid number.");
            }
        }
    }
    
    private void showTransferDialog() {
        JTextField accountField = new JTextField();
        JTextField amountField = new JTextField();
        
        Object[] message = {
            "Recipient Account No:", accountField,
            "Amount (₹):", amountField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Transfer Money", 
            JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                String toAccount = accountField.getText().trim();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    if (bankingService.transfer(user.getAccountNo(), toAccount, amount)) {
                        // Update user balance
                        BigDecimal newBalance = bankingService.getBalance(user.getAccountNo());
                        user.setBalance(newBalance);
                        JOptionPane.showMessageDialog(this, 
                            "<html><b>Transfer Successful!</b><br><br>" +
                            "Amount Transferred: <font color='orange'><b>" + rupeeFormat.format(amount) + "</b></font><br>" +
                            "To Account: " + toAccount + "<br>" +
                            "New Balance: <font color='blue'><b>" + rupeeFormat.format(newBalance) + "</b></font></html>");
                    } else {
                        JOptionPane.showMessageDialog(this, "Transfer failed! Please check recipient account number and your balance.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format! Please enter a valid number.");
            }
        }
    }
    
    private void logout() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}
