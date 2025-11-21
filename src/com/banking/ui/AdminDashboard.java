package com.banking.ui;

import com.banking.dao.TransactionDAO;
import com.banking.dao.UserDAO;
import com.banking.model.User;
import com.banking.model.Transaction;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdminDashboard extends JFrame {
    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public AdminDashboard() {
        this.userDAO = new UserDAO();
        this.transactionDAO = new TransactionDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Admin Dashboard - Banking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        fileMenu.add(logoutItem);
        
        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        JMenuItem userMgmtItem = new JMenuItem("User Management");
        JMenuItem transactionItem = new JMenuItem("Transactions");
        JMenuItem reportsItem = new JMenuItem("Reports");
        
        menuBar.add(fileMenu);
        menuBar.add(dashboardItem);
        menuBar.add(userMgmtItem);
        menuBar.add(transactionItem);
        menuBar.add(reportsItem);
        setJMenuBar(menuBar);
        
        // Main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create different views
        mainPanel.add(createDashboardView(), "DASHBOARD");
        mainPanel.add(createUserManagementView(), "USER_MANAGEMENT");
        mainPanel.add(createTransactionsView(), "TRANSACTIONS");
        mainPanel.add(createReportsView(), "REPORTS");
        
        add(mainPanel);
        
        // Event listeners for menu items
        dashboardItem.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));
        userMgmtItem.addActionListener(e -> {
            refreshUserManagementView();
            cardLayout.show(mainPanel, "USER_MANAGEMENT");
        });
        transactionItem.addActionListener(e -> {
            refreshTransactionsView();
            cardLayout.show(mainPanel, "TRANSACTIONS");
        });
        reportsItem.addActionListener(e -> cardLayout.show(mainPanel, "REPORTS"));
        logoutItem.addActionListener(e -> logout());
    }
    
    private JPanel createDashboardView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard - System Overview", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Get real data
        List<User> users = userDAO.getAllUsers();
        int totalUsers = users.size();
        double totalBalance = 0;
        for (User user : users) {
            totalBalance += user.getBalance().doubleValue();
        }
        int todayTransactions = transactionDAO.getTodayTransactionsCount();
        
        // Create stat cards
        JPanel usersPanel = createStatCard("Total Users", String.valueOf(totalUsers), Color.BLUE);
        JPanel balancePanel = createStatCard("Total Bank Balance", "₹" + totalBalance, Color.GREEN);
        JPanel transactionsPanel = createStatCard("Today's Transactions", String.valueOf(todayTransactions), Color.ORANGE);
        JPanel statusPanel = createStatCard("System Status", "Online", Color.RED);
        
        statsPanel.add(usersPanel);
        statsPanel.add(balancePanel);
        statsPanel.add(transactionsPanel);
        statsPanel.add(statusPanel);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Recent activity panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        
        String[] columns = {"Type", "Account", "Amount", "Date"};
        List<Transaction> recentTransactions = transactionDAO.getRecentTransactions(5);
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (Transaction transaction : recentTransactions) {
            model.addRow(new Object[]{
                transaction.getType(),
                transaction.getAccountNo(),
                "₹" + transaction.getAmount(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm").format(transaction.getTimestamp())
            });
        }
        
        JTable activityTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(activityTable);
        activityPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(activityPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createUserManagementView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Registered Users", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // User table
        String[] columns = {"Account No", "Name", "Email", "Balance", "Actions"};
        List<User> users = userDAO.getAllUsers();
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only actions column is editable
            }
        };
        
        for (User user : users) {
            model.addRow(new Object[]{
                user.getAccountNo(),
                user.getName(),
                user.getEmail(),
                "₹" + user.getBalance(),
                "Edit/Delete"
            });
        }
        
        JTable userTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add New User");
        
        refreshBtn.addActionListener(e -> {
            refreshUserManagementView();
            JOptionPane.showMessageDialog(this, 
                "User list refreshed successfully!", 
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        addBtn.addActionListener(e -> showAddUserDialog());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTransactionsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("All Transactions", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Transaction table
        String[] columns = {"ID", "Account No", "Type", "Amount", "Description", "Date"};
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (Transaction transaction : transactions) {
            model.addRow(new Object[]{
                transaction.getId(),
                transaction.getAccountNo(),
                transaction.getType(),
                "₹" + transaction.getAmount(),
                transaction.getDescription(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(transaction.getTimestamp())
            });
        }
        
        JTable transactionTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout());
        filterPanel.add(new JLabel("Filter by Account:"));
        JTextField filterField = new JTextField(15);
        JButton filterBtn = new JButton("Filter");
        JButton clearBtn = new JButton("Clear Filter");
        JButton refreshBtn = new JButton("Refresh");
        
        filterBtn.addActionListener(e -> filterTransactions(filterField.getText()));
        clearBtn.addActionListener(e -> {
            filterField.setText("");
            refreshTransactionsView();
        });
        
        refreshBtn.addActionListener(e -> {
            refreshTransactionsView();
            JOptionPane.showMessageDialog(this, 
                "Transaction list refreshed successfully!", 
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        filterPanel.add(filterField);
        filterPanel.add(filterBtn);
        filterPanel.add(clearBtn);
        filterPanel.add(refreshBtn);
        
        panel.add(filterPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createReportsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("System Reports", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Reports panel
        JPanel reportsPanel = new JPanel();
        reportsPanel.setLayout(new BoxLayout(reportsPanel, BoxLayout.Y_AXIS));
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        // Create report buttons
        JButton dailyReportBtn = new JButton("Generate Daily Transaction Report");
        JButton userActivityBtn = new JButton("Generate User Activity Report");
        JButton financialReportBtn = new JButton("Generate Financial Summary");
        JButton systemHealthBtn = new JButton("Generate System Health Report");
        
        // Style buttons
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        dailyReportBtn.setFont(buttonFont);
        userActivityBtn.setFont(buttonFont);
        financialReportBtn.setFont(buttonFont);
        systemHealthBtn.setFont(buttonFont);
        
        // Set same size for all buttons
        Dimension buttonSize = new Dimension(300, 35);
        dailyReportBtn.setPreferredSize(buttonSize);
        userActivityBtn.setPreferredSize(buttonSize);
        financialReportBtn.setPreferredSize(buttonSize);
        systemHealthBtn.setPreferredSize(buttonSize);
        
        dailyReportBtn.setMaximumSize(buttonSize);
        userActivityBtn.setMaximumSize(buttonSize);
        financialReportBtn.setMaximumSize(buttonSize);
        systemHealthBtn.setMaximumSize(buttonSize);
        
        // Center align buttons
        dailyReportBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        userActivityBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        financialReportBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        systemHealthBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add buttons with spacing
        reportsPanel.add(dailyReportBtn);
        reportsPanel.add(Box.createVerticalStrut(20));
        reportsPanel.add(userActivityBtn);
        reportsPanel.add(Box.createVerticalStrut(20));
        reportsPanel.add(financialReportBtn);
        reportsPanel.add(Box.createVerticalStrut(20));
        reportsPanel.add(systemHealthBtn);
        
        panel.add(reportsPanel, BorderLayout.CENTER);
        
        // Event listeners
        dailyReportBtn.addActionListener(e -> generateDailyTransactionReport());
        userActivityBtn.addActionListener(e -> generateUserActivityReport());
        financialReportBtn.addActionListener(e -> generateFinancialSummary());
        systemHealthBtn.addActionListener(e -> generateSystemHealthReport());
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void refreshUserManagementView() {
        cardLayout.show(mainPanel, "USER_MANAGEMENT");
    }
    
    private void refreshTransactionsView() {
        cardLayout.show(mainPanel, "TRANSACTIONS");
    }
    
    private void filterTransactions(String accountNo) {
        JOptionPane.showMessageDialog(this, "Filtering transactions for account: " + accountNo);
    }
    
    private void showAddUserDialog() {
        // Create the dialog
        JDialog addUserDialog = new JDialog(this, "Add New User", true);
        addUserDialog.setSize(400, 400);
        addUserDialog.setLocationRelativeTo(this);
        addUserDialog.setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JTextField accountNoField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField ageField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> accountTypeCombo = new JComboBox<>(new String[]{"Savings Account", "Current Account"});
        JTextField initialBalanceField = new JTextField("0");
        
        formPanel.add(new JLabel("Account Number:"));
        formPanel.add(accountNoField);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Age:"));
        formPanel.add(ageField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Account Type:"));
        formPanel.add(accountTypeCombo);
        formPanel.add(new JLabel("Initial Balance (₹):"));
        formPanel.add(initialBalanceField);
        
        addUserDialog.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Save User");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            // Validate and save user
            if (saveNewUser(accountNoField.getText(), nameField.getText(), emailField.getText(),
                    ageField.getText(), new String(passwordField.getPassword()),
                    (String) accountTypeCombo.getSelectedItem(), initialBalanceField.getText())) {
                addUserDialog.dispose();
                refreshUserManagementView();
            }
        });
        
        cancelBtn.addActionListener(e -> addUserDialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        addUserDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        addUserDialog.setVisible(true);
    }
    
    private boolean saveNewUser(String accountNo, String name, String email, String age, 
                               String password, String accountType, String balanceStr) {
        // Validation
        if (accountNo.isEmpty() || name.isEmpty() || email.isEmpty() || age.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            // Check if account number already exists
            if (userDAO.accountExists(accountNo)) {
                JOptionPane.showMessageDialog(this, "Account number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Parse age and balance
            int userAge = Integer.parseInt(age);
            double initialBalance = Double.parseDouble(balanceStr);
            
            if (userAge < 18) {
                JOptionPane.showMessageDialog(this, "User must be at least 18 years old!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (initialBalance < 0) {
                JOptionPane.showMessageDialog(this, "Initial balance cannot be negative!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Create user object
            User newUser = new User();
            newUser.setAccountNo(accountNo);
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setBalance(java.math.BigDecimal.valueOf(initialBalance));
            
            // Save to database
            if (userDAO.registerUser(newUser)) {
                JOptionPane.showMessageDialog(this, 
                    "User created successfully!\n\n" +
                    "Account Number: " + accountNo + "\n" +
                    "Name: " + name + "\n" +
                    "Account Type: " + accountType + "\n" +
                    "Initial Balance: ₹" + initialBalance,
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for age and balance!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // ... (Keep all your existing report generation methods - they remain the same)
    private void generateDailyTransactionReport() {
        generateReport("Daily_Transaction_Report", new ReportWriter() {
            @Override
            public void writeReport(PrintWriter writer) {
                writeDailyTransactionReport(writer);
            }
        });
    }
    
    private void generateUserActivityReport() {
        generateReport("User_Activity_Report", new ReportWriter() {
            @Override
            public void writeReport(PrintWriter writer) {
                writeUserActivityReport(writer);
            }
        });
    }
    
    private void generateFinancialSummary() {
        generateReport("Financial_Summary", new ReportWriter() {
            @Override
            public void writeReport(PrintWriter writer) {
                writeFinancialSummary(writer);
            }
        });
    }
    
    private void generateSystemHealthReport() {
        generateReport("System_Health_Report", new ReportWriter() {
            @Override
            public void writeReport(PrintWriter writer) {
                writeSystemHealthReport(writer);
            }
        });
    }
    
    private void generateReport(String reportName, ReportWriter writer) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = reportName + "_" + timestamp + ".txt";
            File file = new File(fileName);
            
            try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                writer.writeReport(out);
            }
            
            openFileInNotepad(file);
            
            JOptionPane.showMessageDialog(this, 
                reportName + " generated successfully!\nFile: " + file.getAbsolutePath(),
                "Report Generated", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openFileInNotepad(File file) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                Runtime.getRuntime().exec("notepad.exe " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Could not open file in notepad: " + e.getMessage());
        }
    }
    
    private void writeDailyTransactionReport(PrintWriter writer) {
        List<User> users = userDAO.getAllUsers();
        double totalBalance = 0;
        for (User user : users) {
            totalBalance += user.getBalance().doubleValue();
        }
        
        writer.println("DAILY TRANSACTION REPORT");
        writer.println("==================================================");
        writer.println("Generated on: " + new Date());
        writer.println();
        writer.println("SYSTEM STATISTICS:");
        writer.println("- Total Users: " + users.size());
        writer.println("- Total Bank Balance: ₹" + totalBalance);
        writer.println("- Today's Transactions: " + transactionDAO.getTodayTransactionsCount());
        writer.println("- System Status: Online");
    }
    
    private void writeUserActivityReport(PrintWriter writer) {
        List<User> users = userDAO.getAllUsers();
        
        writer.println("USER ACTIVITY REPORT");
        writer.println("========================================");
        writer.println("Generated on: " + new Date());
        writer.println();
        writer.println("REGISTERED USERS:");
        writer.println("--------------------------------------------------------------------------------");
        writer.printf("%-15s %-20s %-25s %-15s%n", "Account No", "Name", "Email", "Balance");
        writer.println("--------------------------------------------------------------------------------");
        
        for (User user : users) {
            writer.printf("%-15s %-20s %-25s ₹%-15s%n",
                user.getAccountNo(),
                user.getName(),
                user.getEmail(),
                user.getBalance().toString());
        }
    }
    
    private void writeFinancialSummary(PrintWriter writer) {
        List<User> users = userDAO.getAllUsers();
        double totalBalance = 0;
        for (User user : users) {
            totalBalance += user.getBalance().doubleValue();
        }
        double avgBalance = users.isEmpty() ? 0 : totalBalance / users.size();
        
        writer.println("FINANCIAL SUMMARY REPORT");
        writer.println("=============================================");
        writer.println("Generated on: " + new Date());
        writer.println();
        writer.println("FINANCIAL OVERVIEW:");
        writer.println("- Total System Balance: ₹" + totalBalance);
        writer.println("- Average Balance per User: ₹" + String.format("%.2f", avgBalance));
        writer.println("- Total Number of Accounts: " + users.size());
    }
    
    private void writeSystemHealthReport(PrintWriter writer) {
        List<User> users = userDAO.getAllUsers();
        
        writer.println("SYSTEM HEALTH REPORT");
        writer.println("========================================");
        writer.println("Generated on: " + new Date());
        writer.println();
        writer.println("SYSTEM STATUS: ONLINE");
        writer.println();
        writer.println("PERFORMANCE METRICS:");
        writer.println("- Total Users: " + users.size());
        writer.println("- Database Connection: Active");
        writer.println("- Transaction System: Operational");
    }
    
    private void logout() {
        new LoginFrame().setVisible(true);
        dispose();
    }
    
    // Interface for report writing
    private interface ReportWriter {
        void writeReport(PrintWriter writer);
    }
}
