package com.restaurant.ui.dialog;

import com.restaurant.data.DataManager;
import com.restaurant.model.Employee;
import com.restaurant.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class EmployeeDialog extends JDialog {
    private Consumer<Employee> onSave;
    private Employee item;

    private JTextField tfId, tfName, tfCccd, tfPhone, tfAddress, tfStartDate;
    private JComboBox<String> cbRole;

    public EmployeeDialog(Window owner, Employee item, Consumer<Employee> onSave) {
        super(owner, item == null ? "Thêm nhân viên" : "Cập nhật nhân viên", ModalityType.APPLICATION_MODAL);
        this.item = item;
        this.onSave = onSave;
        buildUI();
        if (item != null) fillData();
        setSize(500, 460);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JLabel title = new JLabel(item == null ? "Thêm nhân viên mới" : "Cập nhật thông tin nhân viên");
        title.setFont(UIConstants.FONT_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(0, 24, 12, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 6, 7, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        tfId        = field();
        tfName      = field();
        tfCccd      = field();
        tfPhone     = field();
        tfAddress   = field();
        tfStartDate = field();
        cbRole = new JComboBox<>(new String[]{"Phục vụ", "Đầu bếp", "Thu ngân", "Quản lý"});
        cbRole.setFont(UIConstants.FONT_BODY);

        // Auto-generate ID for new employee
        if (item == null) {
            tfId.setText(DataManager.getInstance().generateEmployeeId());
            tfId.setEditable(false);
            tfId.setBackground(new Color(0xF3F4F6));
        }

        addRow(form, gbc, 0, "ID:",           tfId);
        addRow(form, gbc, 1, "Họ và tên:",    tfName);
        addRow(form, gbc, 2, "CCCD:",          tfCccd);
        addRow(form, gbc, 3, "SDT:",           tfPhone);
        addRow(form, gbc, 4, "Địa chỉ:",      tfAddress);
        addRow(form, gbc, 5, "Ngày vào làm:", tfStartDate);
        addRow(form, gbc, 6, "Vai trò:",      cbRole);

        root.add(form, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnBar.setBackground(Color.WHITE);
        btnBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER_COLOR));

        RoundedButton btnCancel = RoundedButton.outline("Hủy");
        btnCancel.setPreferredSize(new Dimension(90, UIConstants.BTN_HEIGHT));
        btnCancel.addActionListener(e -> dispose());

        RoundedButton btnSave = new RoundedButton(item == null ? "Thêm" : "Lưu");
        btnSave.setPreferredSize(new Dimension(100, UIConstants.BTN_HEIGHT));
        btnSave.addActionListener(e -> save());

        btnBar.add(btnCancel);
        btnBar.add(btnSave);
        root.add(btnBar, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void fillData() {
        tfId.setText(item.getId());
        tfId.setEditable(false);
        tfId.setBackground(new Color(0xF3F4F6));
        tfName.setText(item.getName());
        tfCccd.setText(item.getCccd());
        tfPhone.setText(item.getPhone());
        tfAddress.setText(item.getAddress());
        tfStartDate.setText(item.getStartDate());
        cbRole.setSelectedItem(item.getRoleDisplay());
    }

    private void save() {
        String id        = tfId.getText().trim();
        String name      = tfName.getText().trim();
        String cccd      = tfCccd.getText().trim();
        String phone     = tfPhone.getText().trim();
        String address   = tfAddress.getText().trim();
        String startDate = tfStartDate.getText().trim();
        String roleStr   = (String) cbRole.getSelectedItem();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ tên và SDT!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee.Role role;
        switch (roleStr) {
            case "Đầu bếp":  role = Employee.Role.DAU_BEP;  break;
            case "Thu ngân":  role = Employee.Role.THU_NGAN; break;
            case "Quản lý":   role = Employee.Role.QUAN_LY;  break;
            default:           role = Employee.Role.PHUC_VU;
        }

        onSave.accept(new Employee(id, name, cccd, phone, address, startDate, role));
        dispose();
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_BOLD);
        lbl.setPreferredSize(new Dimension(110, 32));
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        comp.setPreferredSize(new Dimension(300, 34));
        form.add(comp, gbc);
    }

    private JTextField field() {
        JTextField tf = new JTextField();
        tf.setFont(UIConstants.FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return tf;
    }
}
