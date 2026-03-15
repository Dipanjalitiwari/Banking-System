package com.bankingsystem.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.bankingsystem.App;
import com.bankingsystem.model.CustomerSummary;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.service.AdminService;
import com.bankingsystem.service.AuthService;
import com.bankingsystem.util.AutoLogoutManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController {
    @FXML
    private TableView<CustomerSummary> customersTable;

    @FXML
    private TableColumn<CustomerSummary, Integer> idColumn;

    @FXML
    private TableColumn<CustomerSummary, String> nameColumn;

    @FXML
    private TableColumn<CustomerSummary, String> usernameColumn;

    @FXML
    private TableColumn<CustomerSummary, String> accountColumn;

    @FXML
    private TableColumn<CustomerSummary, BigDecimal> balanceColumn;

    @FXML
    private TableColumn<CustomerSummary, Boolean> activeColumn;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField newUsernameField;

    @FXML
    private TextField newPasswordField;

    @FXML
    private TextField openingBalanceField;

    @FXML
    private TextArea transactionsArea;

    @FXML
    private Label statusLabel;

    private final AdminService adminService = new AdminService();
    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        startAutoLogoutTimer();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        loadCustomers();
    }

    @FXML
    public void createCustomer() {
        AutoLogoutManager.reset();
        try {
            adminService.createCustomer(
                    fullNameField.getText().trim(),
                    newUsernameField.getText().trim(),
                    newPasswordField.getText().trim(),
                    new BigDecimal(openingBalanceField.getText().trim()));
            statusLabel.setText("Customer created successfully.");
            clearFields();
            loadCustomers();
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    @FXML
    public void activateCustomer() {
        updateCustomerStatus(true);
    }

    @FXML
    public void blockCustomer() {
        updateCustomerStatus(false);
    }

    @FXML
    public void viewTransactions() {
        AutoLogoutManager.reset();
        CustomerSummary selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a customer first.");
            return;
        }

        try {
            List<Transaction> transactions = adminService.getCustomerTransactions(selected.getUserId());
            if (transactions.isEmpty()) {
                transactionsArea.setText("No transactions found.");
                return;
            }

            String lines = transactions.stream()
                    .map(t -> String.format("%s | %s | %.2f | %s",
                            t.getCreatedAt(),
                            t.getType(),
                            t.getAmount(),
                            t.getDescription()))
                    .collect(Collectors.joining("\n"));

            transactionsArea.setText(lines);
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    @FXML
    public void logout() {
        try {
            AutoLogoutManager.stop();
            authService.logout();
            App.showScene("/com/bankingsystem/view/login.fxml", "Banking Management System");
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    private void loadCustomers() {
        try {
            customersTable.setItems(FXCollections.observableArrayList(adminService.getAllCustomers()));
            statusLabel.setText("Loaded customers.");
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    private void updateCustomerStatus(boolean active) {
        AutoLogoutManager.reset();
        CustomerSummary selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a customer first.");
            return;
        }

        try {
            adminService.updateCustomerStatus(selected.getUserId(), active);
            statusLabel.setText(active ? "Account activated." : "Account blocked.");
            loadCustomers();
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    private void clearFields() {
        fullNameField.clear();
        newUsernameField.clear();
        newPasswordField.clear();
        openingBalanceField.clear();
    }

    private void startAutoLogoutTimer() {
        AutoLogoutManager.start(() -> {
            try {
                authService.logout();
                App.showScene("/com/bankingsystem/view/login.fxml", "Banking Management System");
            } catch (Exception ex) {
                statusLabel.setText(ex.getMessage());
            }
        });
    }
}
