package com.nhomdoan;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.util.Optional;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Label emailLabel;

    @FXML
    public void initialize() {
        UserSession session = UserSession.getInstance();
        welcomeLabel.setText("Xin chào, " + session.getName() + "!");
        emailLabel.setText(session.getEmail());
        String role = session.getRoleName();
        roleLabel.setText((role != null && !role.isEmpty()) ? role : "Nhân viên");
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc muốn đăng xuất không?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserSession.clear();
            try {
                App.showLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML private void handleQuanLyBan() { showPlaceholder("Quản lý bàn"); }
    @FXML private void handleQuanLyMon() { showPlaceholder("Quản lý món ăn"); }
    @FXML private void handleQuanLyOrder() { showPlaceholder("Quản lý đơn hàng"); }
    @FXML private void handleThongKe() { showPlaceholder("Thống kê doanh thu"); }
    @FXML private void handleQuanLyNhanVien() { showPlaceholder("Quản lý nhân viên"); }
    @FXML private void handleCaiDat() { showPlaceholder("Cài đặt hệ thống"); }

    private void showPlaceholder(String module) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(module);
        alert.setHeaderText(module);
        alert.setContentText("Chức năng \"" + module + "\" đang được phát triển.");
        alert.showAndWait();
    }
}
