package com.bankingsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private int id;
    private int accountId;
    private TransactionType type;
    private BigDecimal Amount;
    private String description;
    private String referenceAccount;
    private LocalDateTime createdAt;

    public Transaction(int id, int accountId, TransactionType type, BigDecimal Amount, String description,
            String referenceAccount, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.Amount = Amount;
        this.description = description;
        this.referenceAccount = referenceAccount;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return Amount;
    }

    public String getDescription() {
        return description;
    }

    public String getReferenceAccount() {
        return referenceAccount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setAmount(BigDecimal Amount) {
        this.Amount = Amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReferenceAccount(String referenceAccount) {
        this.referenceAccount = referenceAccount;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
