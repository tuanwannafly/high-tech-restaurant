package com.nhomdoan;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Label errorLabel;
    @FXML private Label loadingLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        loadingLabel.setVisible(false);

        // Nhấn Enter để đăng nhập
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
        emailField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) passwordField.requestFocus();
        });
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ email và mật khẩu.");
            return;
        }
        if (!email.contains("@")) {
            showError("Email không hợp lệ.");
            return;
        }

        setLoading(true);

        // Gọi API trên thread riêng tránh block UI
        new Thread(() -> {
            AuthService.LoginResult result = AuthService.login(email, password);
            Platform.runLater(() -> {
                setLoading(false);
                if (result.success) {
                    try {
                        App.showMain(result.session);
                    } catch (Exception ex) {
                        showError("Lỗi mở màn hình chính.");
                    }
                } else {
                    showError(result.message);
                }
            });
        }).start();
    }

    @FXML
    private void goToRegister() {
        try {
            App.showRegister();
        } catch (Exception e) {
            showError("Không thể mở trang đăng ký.");
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void setLoading(boolean loading) {
        loginBtn.setDisable(loading);
        loadingLabel.setVisible(loading);
        if (!loading) errorLabel.setVisible(false);
    }
}
