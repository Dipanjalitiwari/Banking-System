package com.bankingsystem.util;

import com.bankingsystem.model.Account;
import com.bankingsystem.model.User;

public class SessionContext {

    private static User currentUser;
    private static Account currentAccount;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentAccount(Account account) {
        currentAccount = account;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
    }

    public static void clear() {
        currentUser = null;
        currentAccount = null;
    }

}