package com.bankingsystem.model;

import java.math.BigDecimal;

public class CustomerSummary {
    private int userId;
    private String fullName;
    private String username;
    private String accountNumber;
    private BigDecimal balance;
    private boolean active;

    public CustomerSummary() {

    }

    public CustomerSummary(int userId, String fullName, String username, String accountNumber, BigDecimal balance,
            boolean active) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.active = active;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}