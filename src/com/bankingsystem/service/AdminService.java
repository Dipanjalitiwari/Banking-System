package com.bankingsystem.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import com.bankingsystem.dao.AccountDAO;
import com.bankingsystem.dao.TransactionDAO;
import com.bankingsystem.dao.UserDAO;
import com.bankingsystem.model.CustomerSummary;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.util.DBconnection;

public class AdminService {
    private final UserDAO userDAO = new UserDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public void createCustomer(String fullName, String username, String password, BigDecimal openingBalance)
            throws Exception {
        if (openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }

        try (Connection conn = DBconnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = userDAO.createCustomer(conn, fullName, username, password);
                accountDAO.createForCustomer(conn, userId, openingBalance);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<CustomerSummary> getAllCustomers() throws Exception {
        try (Connection conn = DBconnection.getConnection()) {
            return userDAO.fetchAllCustomers(conn);
        }
    }

    public void updateCustomerStatus(int userId, boolean active) throws Exception {
        try (Connection conn = DBconnection.getConnection()) {
            userDAO.updateCustomerStatus(conn, userId, active);
        }
    }

    public List<Transaction> getCustomerTransactions(int userId) throws Exception {
        try (Connection conn = DBconnection.getConnection()) {
            return transactionDAO.findByUserId(conn, userId);
        }
    }
}