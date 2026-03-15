package com.bankingsystem.service;

import java.sql.Connection;

import com.bankingsystem.dao.AccountDAO;
import com.bankingsystem.dao.UserDAO;
import com.bankingsystem.model.Account;
import com.bankingsystem.model.User;
import com.bankingsystem.util.DBconnection;
import com.bankingsystem.util.SessionContext;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public User login(String username, String password) throws Exception {
        try (Connection conn = DBconnection.getConnection()) {

            User user = userDAO.findByUsernameAndPassword(conn, username, password);

            if (user == null) {
                throw new IllegalArgumentException("Invalid Credentials");
            }

            if (!user.isActive()) {
                throw new IllegalStateException("Account is Blocked. Contact Admin.");

            }
            if (user.getRole().name().equals("CUSTOMER")) {

            }

            SessionContext.setCurrentUser(user);
            if (user.getRole().name().equals("CUSTOMER")) {
                Account account = accountDAO.findByUserId(conn, user.getId());
                SessionContext.setCurrentAccount(account);

            }

            return user;
        }
    }

    public void logout() {
        SessionContext.clear();
    }

}