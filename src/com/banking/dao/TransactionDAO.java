/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banking.dao;

import com.banking.database.DBConnection;
import com.banking.model.Transaction;
import java.sql.*;
import java.math.BigDecimal;  // ADD THIS IMPORT
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    
    public boolean recordTransaction(String accountNo, String type, BigDecimal amount, String description) {
        String sql = "INSERT INTO transactions (account_no, type, amount, description, transaction_date) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNo);
            stmt.setString(2, type);
            stmt.setBigDecimal(3, amount);
            stmt.setString(4, description);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all transactions for admin view
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setAccountNo(rs.getString("account_no"));
                transaction.setType(rs.getString("type"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setDescription(rs.getString("description"));
                transaction.setTimestamp(rs.getTimestamp("transaction_date"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    // Get transactions by account number
    public List<Transaction> getTransactionsByAccount(String accountNo) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_no = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNo);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setAccountNo(rs.getString("account_no"));
                transaction.setType(rs.getString("type"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setDescription(rs.getString("description"));
                transaction.setTimestamp(rs.getTimestamp("transaction_date"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    // Get today's transactions count
    public int getTodayTransactionsCount() {
        String sql = "SELECT COUNT(*) as count FROM transactions WHERE DATE(transaction_date) = CURRENT_DATE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get total transaction amount for a specific period (useful for reports)
    public BigDecimal getTotalTransactionAmount(String type, String period) {
        String sql = "SELECT SUM(amount) as total FROM transactions WHERE type = ? ";
        
        switch (period.toLowerCase()) {
            case "today":
                sql += "AND DATE(transaction_date) = CURRENT_DATE";
                break;
            case "month":
                sql += "AND MONTH(transaction_date) = MONTH(CURRENT_DATE) AND YEAR(transaction_date) = YEAR(CURRENT_DATE)";
                break;
            case "year":
                sql += "AND YEAR(transaction_date) = YEAR(CURRENT_DATE)";
                break;
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    // Get recent transactions (for dashboard)
    public List<Transaction> getRecentTransactions(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setAccountNo(rs.getString("account_no"));
                transaction.setType(rs.getString("type"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setDescription(rs.getString("description"));
                transaction.setTimestamp(rs.getTimestamp("transaction_date"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
