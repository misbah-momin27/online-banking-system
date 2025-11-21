/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banking.dao;

import com.banking.database.DBConnection;
import com.banking.model.Admin;
import java.sql.*;

public class AdminDAO {
    
    public Admin loginAdmin(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        System.out.println("Attempting admin login: " + username);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Admin login successful for: " + username);
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                return admin;
            } else {
                System.out.println("No admin found with these credentials");
            }
            
        } catch (SQLException e) {
            System.out.println("Database error in admin login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Method to create default admin if not exists
    public boolean createDefaultAdmin() {
        String sql = "INSERT OR IGNORE INTO admin (username, password) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "admin");
            stmt.setString(2, "admin123");
            
            int result = stmt.executeUpdate();
            System.out.println("Default admin creation result: " + result);
            return result > 0;
            
        } catch (SQLException e) {
            System.out.println("Error creating default admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get admin by username
    public Admin getAdminByUsername(String username) {
        String sql = "SELECT * FROM admin WHERE username = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                return admin;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
