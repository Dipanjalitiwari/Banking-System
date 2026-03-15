package com.bankingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bankingsystem.model.CustomerSummary;
import com.bankingsystem.model.Role;
import com.bankingsystem.model.User;

public class UserDAO {

    public User findByUsernameAndPassword(Connection conn, String username, String password) throws SQLException {
        String sql = "SELECT id, full_name, username, password, role, is_active FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public int createCustomer(Connection conn, String fullName, String username, String password) throws SQLException {
        String sql = "INSERT INTO users(full_name, username, password, role, is_active) VALUES (?, ?, ?, 'CUSTOMER', true)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fullName);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Could not create customer");
    }

    public List<CustomerSummary> fetchAllCustomers(Connection conn) throws SQLException {
        String sql = """
                SELECT u.id, u.full_name, u.username, a.account_number, a.balance, u.is_active
                FROM users u
                JOIN accounts a ON a.user_id = u.id
                WHERE u.role = 'CUSTOMER'
                ORDER BY u.id
                """;

        List<CustomerSummary> customers = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(new CustomerSummary(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("account_number"),
                        rs.getBigDecimal("balance"),
                        rs.getBoolean("is_active")));
            }
        }
        return customers;
    }

    public void updateCustomerStatus(Connection conn, int userId, boolean active) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE id = ? AND role = 'CUSTOMER'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, active);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("username"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role")),
                rs.getBoolean("is_active"));
    }
}
