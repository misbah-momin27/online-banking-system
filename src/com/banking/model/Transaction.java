/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banking.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int id;
    private String accountNo;
    private String type;
    private BigDecimal amount;
    private String description;
    private Timestamp timestamp;
    
    // Constructors
    public Transaction() {}
    
    public Transaction(int id, String accountNo, String type, BigDecimal amount, String description, Timestamp timestamp) {
        this.id = id;
        this.accountNo = accountNo;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
