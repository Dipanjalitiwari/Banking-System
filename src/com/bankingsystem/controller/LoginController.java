package com.bankingsystem.controller;

import com.bankingsystem.App;
import com.bankingsystem.model.Role;
import com.bankingsystem.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import com.bankingsystem.service.AuthService;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin() {
        try {
            User user = authService.login(usernameField.getText(), passwordField.getText());

            if (user.getRole() == Role.ADMIN) {
                App.showScene("/com/bankingsystem/view/admin.fxml", "Admin Dashboard");

            } else if (user.getRole() == Role.CUSTOMER) {
                App.showScene("/com/bankingsystem/view/customer.fxml", "Customer Dashboard");
            }

        } catch (Exception e) {
            statusLabel.setText(e.getMessage());
        }
    }
}
