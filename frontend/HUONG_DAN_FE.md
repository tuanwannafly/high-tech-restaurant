# Hướng dẫn chạy FE (Java Swing)

## Yêu cầu
- Java 11+
- Maven 3.6+
- BE Spring Boot đang chạy tại http://localhost:8080

## Build & Run

### 1. Build fat JAR
```bash
mvn clean package -DskipTests
```
JAR được tạo tại `target/RestaurantManagement.jar`

### 2. Chạy (BE mặc định http://localhost:8080)
```bash
java -jar target/RestaurantManagement.jar
```

### 3. Chạy với BE ở địa chỉ khác
```bash
java -Dapi.base.url=http://192.168.1.100:8080 -jar target/RestaurantManagement.jar
```

## Luồng hoạt động
1. App hiện màn hình **Đăng nhập**
2. Nhập email + password của user đã tạo trong BE
3. Sau khi đăng nhập thành công → vào **MainFrame**
4. Tất cả dữ liệu (Menu, Bàn, Đơn hàng, Nhân viên) được tải từ BE

## Files thay đổi so với bản gốc
| File | Thay đổi |
|------|---------|
| `src/.../data/AuthManager.java` | **Mới** - Lưu JWT token |
| `src/.../data/ApiClient.java` | **Mới** - HTTP client gọi REST API |
| `src/.../data/DataManager.java` | **Viết lại** - Delegate sang ApiClient |
| `src/.../ui/LoginDialog.java` | **Mới** - Màn hình đăng nhập |
| `src/.../Main.java` | **Cập nhật** - Hiện LoginDialog trước |
| `src/.../ui/MainFrame.java` | **Cập nhật** - Logout gọi API |
| `pom.xml` | **Cập nhật** - Thêm jackson-databind + maven-shade-plugin |

## Lưu ý
- User đăng nhập phải được gán `restaurant` trong database (qua BE)
- Nếu không gán restaurant, các màn hình Menu/Bàn/Đơn hàng sẽ trống
