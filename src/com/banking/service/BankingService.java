/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banking.service;

import com.banking.dao.TransactionDAO;
import com.banking.dao.UserDAO;
import com.banking.model.User;
import java.math.BigDecimal;

public class BankingService {
    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;
    
    public BankingService() {
        this.userDAO = new UserDAO();
        this.transactionDAO = new TransactionDAO();
    }
    
    public boolean deposit(String accountNo, BigDecimal amount) {
        User user = userDAO.getUserByAccountNo(accountNo);
        if (user != null) {
            BigDecimal newBalance = user.getBalance().add(amount);
            if (userDAO.updateBalance(accountNo, newBalance)) {
                return transactionDAO.recordTransaction(accountNo, "DEPOSIT", amount, 
                    "Deposit: ₹" + amount);
            }
        }
        return false;
    }
    
    public boolean withdraw(String accountNo, BigDecimal amount) {
        User user = userDAO.getUserByAccountNo(accountNo);
        if (user != null && user.getBalance().compareTo(amount) >= 0) {
            BigDecimal newBalance = user.getBalance().subtract(amount);
            if (userDAO.updateBalance(accountNo, newBalance)) {
                return transactionDAO.recordTransaction(accountNo, "WITHDRAW", amount, 
                    "Withdrawal: ₹" + amount);
            }
        }
        return false;
    }
    
    public boolean transfer(String fromAccount, String toAccount, BigDecimal amount) {
        User fromUser = userDAO.getUserByAccountNo(fromAccount);
        User toUser = userDAO.getUserByAccountNo(toAccount);
        
        if (fromUser != null && toUser != null && 
            fromUser.getBalance().compareTo(amount) >= 0) {
            
            // Withdraw from sender
            BigDecimal fromNewBalance = fromUser.getBalance().subtract(amount);
            // Deposit to receiver
            BigDecimal toNewBalance = toUser.getBalance().add(amount);
            
            if (userDAO.updateBalance(fromAccount, fromNewBalance) && 
                userDAO.updateBalance(toAccount, toNewBalance)) {
                
                transactionDAO.recordTransaction(fromAccount, "TRANSFER", amount, 
                    "Transfer to " + toAccount + ": ₹" + amount);
                transactionDAO.recordTransaction(toAccount, "TRANSFER", amount, 
                    "Transfer from " + fromAccount + ": ₹" + amount);
                
                return true;
            }
        }
        return false;
    }

    public BigDecimal getBalance(String accountNo) {
        User user = userDAO.getUserByAccountNo(accountNo);
        if (user != null) {
            return user.getBalance();
        }
        return BigDecimal.ZERO; // Return zero if user not found
    }
}
