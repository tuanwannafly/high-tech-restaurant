# Restaurant Management System - Java Swing

## Yêu cầu
- Java 11 trở lên (https://www.java.com/download)

## Chạy ứng dụng

### Windows
Nhấp đúp vào `run.bat` hoặc chạy:
```
java -jar RestaurantManagement.jar
```

### Linux / macOS
```
chmod +x run.sh
./run.sh
```

## Tính năng
- **Home**: Tổng quan doanh thu, đơn hàng, bàn đang phục vụ
- **Quản lý Menu**: Thêm/Xóa/Cập nhật/Xem chi tiết món ăn, tìm kiếm & lọc, thống kê
- **Quản lý Bàn**: Thêm/Xóa/Cập nhật bàn, lọc theo sức chứa & trạng thái
- **Quản lý Nhân viên**: Thêm/Xóa/Cập nhật/Xem chi tiết nhân viên, lọc theo vai trò
- **Quản lý Đơn hàng**: Xem đơn hàng, cập nhật trạng thái, xem chi tiết & thống kê

## Cấu trúc dự án
```
src/main/java/com/restaurant/
├── Main.java                    # Entry point
├── data/DataManager.java        # Quản lý dữ liệu
├── model/                       # Data models
│   ├── MenuItem.java
│   ├── TableItem.java
│   ├── Employee.java
│   └── Order.java
└── ui/                          # Giao diện
    ├── MainFrame.java           # Cửa sổ chính + navigation
    ├── HomePanel.java           # Màn hình tổng quan
    ├── MenuPanel.java           # Quản lý menu
    ├── TablePanel.java          # Quản lý bàn
    ├── EmployeePanel.java       # Quản lý nhân viên
    ├── OrderPanel.java          # Quản lý đơn hàng
    └── dialog/                  # Các hộp thoại
        ├── MenuDialog.java
        ├── MenuDetailDialog.java
        ├── MenuStatDialog.java
        ├── TableDialog.java
        ├── EmployeeDialog.java
        ├── EmployeeDetailDialog.java
        ├── OrderDetailDialog.java
        └── OrderStatDialog.java
```
