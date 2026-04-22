package com.restaurant.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HeaderPanel extends JPanel {

    public interface NavListener {
        void onNavigate(String page);
    }

    private NavListener listener;
    private String currentPage = "home";

    private RoundedButton btnHome, btnMenu, btnBan, btnNhanVien, btnDonHang, btnCheDoLamViec;

    public HeaderPanel(NavListener listener) {
        this.listener = listener;
        setBackground(UIConstants.BG_WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(0, 32, 0, 32)
        ));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 56));

        // Logo left
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        JLabel logoIcon = new JLabel("⛁  ");
        logoIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoIcon.setForeground(UIConstants.PRIMARY);
        JLabel logoText = new JLabel("Tên hệ thống");
        logoText.setFont(UIConstants.FONT_LOGO);
        logoText.setForeground(UIConstants.PRIMARY);
        logoPanel.add(logoIcon);
        logoPanel.add(logoText);

        // Logo right
        JPanel rightLogo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightLogo.setOpaque(false);
        JLabel globeIcon = new JLabel("⊕  ");
        globeIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        globeIcon.setForeground(UIConstants.PRIMARY);
        JLabel restaurantName = new JLabel("Tên nhà hàng");
        restaurantName.setFont(UIConstants.FONT_LOGO);
        restaurantName.setForeground(UIConstants.PRIMARY);
        rightLogo.add(globeIcon);
        rightLogo.add(restaurantName);

        add(logoPanel, BorderLayout.WEST);
        add(rightLogo, BorderLayout.EAST);
    }

    public JPanel buildNavBar() {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        navBar.setBackground(UIConstants.BG_WHITE);
        navBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(0, 32, 0, 32)
        ));

        btnHome        = navBtn("🏠 Home",         "home");
        btnMenu        = navBtn("Menu",             "menu");
        btnBan         = navBtn("Bàn",              "ban");
        btnNhanVien    = navBtn("Nhân viên",        "nhanvien");
        btnDonHang     = navBtn("Đơn hàng",         "donhang");
        btnCheDoLamViec= navBtn("Chế độ làm việc",  "chedomlamviec");

        navBar.add(btnHome);
        navBar.add(btnMenu);
        navBar.add(btnBan);
        navBar.add(btnNhanVien);
        navBar.add(btnDonHang);
        navBar.add(btnCheDoLamViec);

        setActivePage("home");
        return navBar;
    }

    private RoundedButton navBtn(String text, String page) {
        RoundedButton btn = new RoundedButton(text);
        btn.setPreferredSize(new Dimension(text.length() * 9 + 20, UIConstants.BTN_HEIGHT));
        btn.addActionListener(e -> {
            setActivePage(page);
            listener.onNavigate(page);
        });
        return btn;
    }

    public void setActivePage(String page) {
        this.currentPage = page;
        Color inactive = new Color(0x6B9FE8);
        for (RoundedButton btn : new RoundedButton[]{btnHome, btnMenu, btnBan, btnNhanVien, btnDonHang, btnCheDoLamViec}) {
            if (btn != null) btn.setBackground(inactive);
        }
    }
}
