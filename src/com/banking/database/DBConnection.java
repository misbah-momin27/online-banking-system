/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banking.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:banking_system.db";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(URL);
            initializeDatabase(conn);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found", e);
        }
    }
    
    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Create admin table
            String createAdminTable = "CREATE TABLE IF NOT EXISTS admin (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL)";
            
            // Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "account_no TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "balance REAL DEFAULT 0.00, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            
            // Create transactions table
            String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "account_no TEXT, " +
                    "type TEXT, " +
                    "amount REAL, " +
                    "description TEXT, " +
                    "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (account_no) REFERENCES users(account_no))";
            
            // Execute table creation
            stmt.execute(createAdminTable);
            stmt.execute(createUsersTable);
            stmt.execute(createTransactionsTable);
            
            // Insert default admin - FIXED SYNTAX
            String insertAdmin = "INSERT OR IGNORE INTO admin (username, password) VALUES ('admin', 'admin123')";
            stmt.execute(insertAdmin);
            
            System.out.println("SQLite Database initialized successfully!");
            System.out.println("Default admin created: admin / admin123");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
