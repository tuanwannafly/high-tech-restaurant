package nhomlamdoan.smartrestaurant.util;

/**
 * Tập hợp hằng số tên role dùng trong toàn bộ hệ thống.
 * Cập nhật tại đây khi tên role thay đổi trong DB.
 */
public final class RoleConstants {

    private RoleConstants() {}

    /** Quản trị hệ thống — bypass mọi permission check */
    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    /** Quản lý nhà hàng — toàn quyền trong phạm vi nhà hàng */
    public static final String RESTAURANT_ADMIN = "RESTAURANT_ADMIN";

    /** Nhân viên phục vụ — tạo/xem đơn hàng, cập nhật trạng thái bàn */
    public static final String WAITER = "WAITER";

    /** Thu ngân — xem đơn hàng, xử lý thanh toán, mở/đóng bàn */
    public static final String CASHIER = "CASHIER";

    /** Đầu bếp — xem và cập nhật trạng thái món trong bếp */
    public static final String CHEF = "CHEF";
}
