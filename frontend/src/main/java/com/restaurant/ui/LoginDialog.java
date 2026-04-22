package com.restaurant.ui;

import com.restaurant.data.ApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Dialog đăng nhập. Hiển thị trước MainFrame.
 * Chặn luồng cho đến khi đăng nhập thành công hoặc người dùng đóng cửa sổ.
 */
public class LoginDialog extends JDialog {

    private boolean loginSuccess = false;

    private JTextField     tfUsername;
    private JPasswordField tfPassword;
    private JLabel         lblError;
    private JButton        btnLogin;

    public LoginDialog(Frame owner) {
        super(owner, "Đăng nhập hệ thống", true); // modal
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 380);
        setResizable(false);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // ─── Header ─────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(UIConstants.PRIMARY);
        header.setPreferredSize(new Dimension(0, 90));

        JPanel logoWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        logoWrap.setOpaque(false);
        JLabel icon = new JLabel("⛁");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        icon.setForeground(Color.WHITE);
        JLabel title = new JLabel("Quản lý Nhà hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        logoWrap.add(icon);
        logoWrap.add(title);
        header.add(logoWrap);
        root.add(header, BorderLayout.NORTH);

        // ─── Form ────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(28, 40, 20, 40));

        form.add(label("Tên đăng nhập / Email"));
        form.add(Box.createVerticalStrut(6));
        tfUsername = styledTextField();
        form.add(tfUsername);

        form.add(Box.createVerticalStrut(16));
        form.add(label("Mật khẩu"));
        form.add(Box.createVerticalStrut(6));
        tfPassword = new JPasswordField();
        styleInput(tfPassword);
        form.add(tfPassword);

        form.add(Box.createVerticalStrut(8));
        lblError = new JLabel(" ");
        lblError.setFont(UIConstants.FONT_SMALL);
        lblError.setForeground(UIConstants.DANGER);
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        form.add(lblError);

        form.add(Box.createVerticalStrut(14));
        btnLogin = new RoundedButton("Đăng nhập");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setAlignmentX(LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> doLogin());
        form.add(btnLogin);

        root.add(form, BorderLayout.CENTER);

        // ─── Footer ──────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Color.WHITE);
        JLabel hint = new JLabel("SmartRestaurant v1.0  •  © 2025");
        hint.setFont(UIConstants.FONT_SMALL);
        hint.setForeground(UIConstants.TEXT_SECONDARY);
        footer.add(hint);
        root.add(footer, BorderLayout.SOUTH);

        // Enter key trên cả hai field
        ActionListener onEnter = e -> doLogin();
        tfUsername.addActionListener(onEnter);
        tfPassword.addActionListener(onEnter);
    }

    private void doLogin() {
        String user = tfUsername.getText().trim();
        String pass = new String(tfPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");
        lblError.setText(" ");

        // Chạy call HTTP ở background thread, tránh block EDT
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override protected String doInBackground() {
                return ApiClient.getInstance().login(user, pass);
            }
            @Override protected void done() {
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");
                try {
                    String token = get();
                    if (token != null && !token.isEmpty()) {
                        loginSuccess = true;
                        dispose();
                    } else {
                        lblError.setText("Sai tên đăng nhập hoặc mật khẩu.");
                        tfPassword.setText("");
                        tfPassword.requestFocus();
                    }
                } catch (Exception ex) {
                    lblError.setText("Không thể kết nối đến máy chủ. Vui lòng thử lại.");
                }
            }
        };
        worker.execute();
    }

    public boolean isLoginSuccess() { return loginSuccess; }

    // ─── Helpers ────────────────────────────────────────────────
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.FONT_BODY);
        l.setForeground(UIConstants.TEXT_PRIMARY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledTextField() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    private void styleInput(JComponent c) {
        c.setFont(UIConstants.FONT_BODY);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        c.setAlignmentX(LEFT_ALIGNMENT);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
    }
}
