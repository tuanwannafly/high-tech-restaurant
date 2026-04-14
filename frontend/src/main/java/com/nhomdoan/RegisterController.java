package com.nhomdoan;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField phoneField;
    @FXML private TextField ageField;
    @FXML private Button registerBtn;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Label loadingLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
        loadingLabel.setVisible(false);

        confirmPasswordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleRegister();
        });
    }

    @FXML
    private void handleRegister() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();
        String phone    = phoneField.getText().trim();
        String ageText  = ageField.getText().trim();

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ họ tên, email và mật khẩu.");
            return;
        }
        if (!email.contains("@")) {
            showError("Email không hợp lệ.");
            return;
        }
        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Mật khẩu xác nhận không khớp.");
            return;
        }

        int age = 0;
        if (!ageText.isEmpty()) {
            try {
                age = Integer.parseInt(ageText);
                if (age < 16 || age > 100) {
                    showError("Tuổi không hợp lệ (16–100).");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Tuổi phải là số.");
                return;
            }
        }

        final int finalAge = age;
        setLoading(true);

        new Thread(() -> {
            AuthService.RegisterResult result = AuthService.register(name, email, password, phone, finalAge);
            Platform.runLater(() -> {
                setLoading(false);
                if (result.success) {
                    showSuccess("Đăng ký thành công! Đang chuyển về trang đăng nhập...");
                    // Tự động về login sau 1.5s
                    new Thread(() -> {
                        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                        Platform.runLater(() -> {
                            try { App.showLogin(); } catch (Exception ignored) {}
                        });
                    }).start();
                } else {
                    showError(result.message);
                }
            });
        }).start();
    }

    @FXML
    private void goToLogin() {
        try {
            App.showLogin();
        } catch (Exception e) {
            showError("Không thể mở trang đăng nhập.");
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String msg) {
        successLabel.setText(msg);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }

    private void setLoading(boolean loading) {
        registerBtn.setDisable(loading);
        loadingLabel.setVisible(loading);
    }
}
