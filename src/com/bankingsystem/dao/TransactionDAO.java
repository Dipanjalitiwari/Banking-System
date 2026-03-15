package com.bankingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.bankingsystem.model.Transaction;
import com.bankingsystem.model.TransactionType;

public class TransactionDAO {

    public void create(Connection conn, Transaction transaction) throws SQLException {
        String sql = """
                INSERT INTO transactions(account_id, type, amount, description, reference_account, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transaction.getAccountId());
            stmt.setString(2, transaction.getType().name());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setString(4, transaction.getDescription());
            stmt.setString(5, transaction.getReferenceAccount());
            stmt.setTimestamp(6, Timestamp.valueOf(transaction.getCreatedAt()));
            stmt.executeUpdate();
        }
    }

    public List<Transaction> findByUserId(Connection conn, int userId) throws SQLException {
        String sql = """
                SELECT t.id, t.account_id, t.type, t.amount, t.description, t.reference_account, t.created_at
                FROM transactions t
                JOIN accounts a ON a.id = t.account_id
                WHERE a.user_id = ?
                ORDER BY t.created_at DESC
                """;
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRow(rs));
                }
            }
        }
        return transactions;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getInt("account_id"),
                TransactionType.valueOf(rs.getString("type")),
                rs.getBigDecimal("amount"),
                rs.getString("description"),
                rs.getString("reference_account"),
                rs.getTimestamp("created_at").toLocalDateTime());
    }
}