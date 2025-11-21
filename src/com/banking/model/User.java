package com.banking.model;

import java.math.BigDecimal;

public class User {
    private String accountNo;
    private String name;
    private String email;
    private String password;
    private BigDecimal balance;
    
    // Default constructor
    public User() {}
    
    // Constructor with parameters
    public User(String accountNo, String name, String email, String password, BigDecimal balance) {
        this.accountNo = accountNo;
        this.name = name;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }
    
    // Getters and setters
    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
