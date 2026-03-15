
package com.bankingsystem.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import com.bankingsystem.dao.AccountDAO;
import com.bankingsystem.dao.TransactionDAO;
import com.bankingsystem.exception.InsufficientBalanceException;
import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.model.TransactionType;
import com.bankingsystem.util.DBconnection;
import com.bankingsystem.util.SessionContext;

public class CustomerService {
    private static final Object TRANSFER_LOCK = new Object();

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public Account refreshCurrentAccount() throws Exception {
        try (Connection conn = DBconnection.getConnection()) {
            Account account = accountDAO.findByUserId(conn, SessionContext.getCurrentUser().getId());
            SessionContext.setCurrentAccount(account);
            return account;
        }
    }

    public void deposit(BigDecimal amount) throws Exception {
        validatePositiveAmount(amount);

        try (Connection conn = DBconnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Account account = accountDAO.findByUserId(conn, SessionContext.getCurrentUser().getId());
                BigDecimal newBalance = account.getBalance().add(amount);
                accountDAO.updateBalance(conn, account.getId(), newBalance);

                transactionDAO.create(conn, new Transaction(
                        0,
                        account.getId(),
                        TransactionType.DEPOSIT,
                        amount,
                        "Cash deposit",
                        null,
                        LocalDateTime.now()));

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void withdraw(BigDecimal amount) throws Exception {
        validatePositiveAmount(amount);

        try (Connection conn = DBconnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Account account = accountDAO.findByUserId(conn, SessionContext.getCurrentUser().getId());
                if (account.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance for withdrawal.");
                }

                BigDecimal newBalance = account.getBalance().subtract(amount);
                accountDAO.updateBalance(conn, account.getId(), newBalance);

                transactionDAO.create(conn, new Transaction(
                        0,
                        account.getId(),
                        TransactionType.WITHDRAW,
                        amount,
                        "Cash withdrawal",
                        null,
                        LocalDateTime.now()));

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void transfer(String destinationAccountNumber, BigDecimal amount) throws Exception {
        validatePositiveAmount(amount);

        synchronized (TRANSFER_LOCK) {
            try (Connection conn = DBconnection.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    Account source = accountDAO.findByUserId(conn, SessionContext.getCurrentUser().getId());
                    Account target = accountDAO.findByAccountNumber(conn, destinationAccountNumber);

                    if (target == null) {
                        throw new IllegalArgumentException("Destination account not found.");
                    }
                    if (source.getAccountNumber().equals(target.getAccountNumber())) {
                        throw new IllegalArgumentException("Cannot transfer to same account.");
                    }
                    if (source.getBalance().compareTo(amount) < 0) {
                        throw new InsufficientBalanceException("Insufficient balance for transfer.");
                    }

                    BigDecimal sourceNewBalance = source.getBalance().subtract(amount);
                    BigDecimal targetNewBalance = target.getBalance().add(amount);

                    accountDAO.updateBalance(conn, source.getId(), sourceNewBalance);
                    accountDAO.updateBalance(conn, target.getId(), targetNewBalance);

                    transactionDAO.create(conn, new Transaction(
                            0,
                            source.getId(),
                            TransactionType.TRANSFER_OUT,
                            amount,
                            "Transfer to " + target.getAccountNumber(),
                            target.getAccountNumber(),
                            LocalDateTime.now()));

                    transactionDAO.create(conn, new Transaction(
                            0,
                            target.getId(),
                            TransactionType.TRANSFER_IN,
                            amount,
                            "Transfer from " + source.getAccountNumber(),
                            source.getAccountNumber(),
                            LocalDateTime.now()));

                    conn.commit();
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            }
        }
    }

    public List<Transaction> getMyTransactions() throws Exception {
        try (Connection conn = DBconnection.getConnection()) {
            return transactionDAO.findByUserId(conn, SessionContext.getCurrentUser().getId());
        }
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}