package com.bankingsystem.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import com.bankingsystem.App;
import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.model.TransactionType;
import com.bankingsystem.service.AuthService;
import com.bankingsystem.service.CustomerService;
import com.bankingsystem.util.AutoLogoutManager;
import com.bankingsystem.util.SessionContext;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class CustomerController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label accountNumberLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label totalDepositsLabel;

    @FXML
    private Label totalWithdrawalsLabel;

    @FXML
    private Label logoutTimerLabel;

    @FXML
    private TextField depositField;

    @FXML
    private TextField withdrawField;

    @FXML
    private TextField transferAccountField;

    @FXML
    private TextField transferAmountField;

    @FXML
    private TextArea historyArea;

    @FXML
    private Label statusLabel;

    private final CustomerService customerService = new CustomerService();
    private final AuthService authService = new AuthService();
    private Timeline countdownTimeline;
    private int remainingSeconds = 300;

    @FXML
    public void initialize() {
        startAutoLogoutTimer();
        welcomeLabel.setText("Welcome, " + SessionContext.getCurrentUser().getFullName());
        refreshAccountDetails();
        loadHistory();
    }

    @FXML
    public void deposit() {
        resetInactivityTimer();
        try {
            customerService.deposit(new BigDecimal(depositField.getText().trim()));
            statusLabel.setText("Deposit successful.");
            depositField.clear();
            refreshAccountDetails();
            loadHistory();
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    @FXML
    public void withdraw() {
        resetInactivityTimer();
        try {
            customerService.withdraw(new BigDecimal(withdrawField.getText().trim()));
            statusLabel.setText("Withdrawal successful.");
            withdrawField.clear();
            refreshAccountDetails();
            loadHistory();
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    @FXML
    public void transfer() {
        resetInactivityTimer();
        try {
            customerService.transfer(
                    transferAccountField.getText().trim(),
                    new BigDecimal(transferAmountField.getText().trim()));
            statusLabel.setText("Transfer successful.");
            transferAccountField.clear();
            transferAmountField.clear();
            refreshAccountDetails();
            loadHistory();
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    @FXML
    public void logout() {
        try {
            AutoLogoutManager.stop();
            stopCountdown();
            authService.logout();
            App.showScene("/com/bankingsystem/view/login.fxml", "Banking Management System");
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    private void refreshAccountDetails() {
        try {
            Account account = customerService.refreshCurrentAccount();
            accountNumberLabel.setText("Account: " + account.getAccountNumber());
            balanceLabel.setText("Balance: " + account.getBalance());
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    private void loadHistory() {
        try {
            List<Transaction> history = customerService.getMyTransactions();
            updateTotals(history);
            if (history.isEmpty()) {
                historyArea.setText("No transactions yet.");
                return;
            }

            String lines = history.stream()
                    .map(t -> String.format("%s | %s | %.2f | %s",
                            t.getCreatedAt(),
                            t.getType(),
                            t.getAmount(),
                            t.getDescription()))
                    .collect(Collectors.joining("\n"));
            historyArea.setText(lines);
        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    private void updateTotals(List<Transaction> history) {
        BigDecimal totalDeposits = history.stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawals = history.stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAW)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalDepositsLabel.setText("Total Deposits: " + formatAmount(totalDeposits));
        totalWithdrawalsLabel.setText("Total Withdrawals: " + formatAmount(totalWithdrawals));
    }

    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private void startAutoLogoutTimer() {
        resetCountdown();
        startCountdown();
        AutoLogoutManager.start(() -> {
            try {
                stopCountdown();
                authService.logout();
                App.showScene("/com/bankingsystem/view/login.fxml", "Banking Management System");
            } catch (Exception ex) {
                statusLabel.setText(ex.getMessage());
            }
        });
    }

    private void resetInactivityTimer() {
        AutoLogoutManager.reset();
        resetCountdown();
    }

    private void startCountdown() {
        stopCountdown();
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> tickCountdown()));
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    private void stopCountdown() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
        }
    }

    private void resetCountdown() {
        remainingSeconds = 300;
        updateCountdownLabel();
    }

    private void tickCountdown() {
        if (remainingSeconds > 0) {
            remainingSeconds--;
            updateCountdownLabel();
        }
    }

    private void updateCountdownLabel() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        logoutTimerLabel.setText(String.format("Auto logout in: %02d:%02d", minutes, seconds));
    }
}