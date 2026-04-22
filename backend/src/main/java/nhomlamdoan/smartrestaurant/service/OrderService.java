package nhomlamdoan.smartrestaurant.service;

import java.util.List;

import nhomlamdoan.smartrestaurant.domain.Order;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.request.order.ReqOrder;
import nhomlamdoan.smartrestaurant.domain.response.order.ResOrder;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;
import nhomlamdoan.smartrestaurant.util.error.PermissionException;

public interface OrderService {

    /** Tạo đơn hàng mới thuộc nhà hàng, gán waiter là người dùng hiện tại */
    ResOrder createOrder(ReqOrder req, Restaurant restaurant, User waiter);

    /**
     * Cập nhật đơn hàng — kiểm tra đơn hàng phải thuộc nhà hàng hiện tại.
     * Kiểm tra quyền chi tiết theo role được thực hiện ở tầng Controller.
     */
    ResOrder updateOrderWithOwnershipCheck(Long id, ReqOrder req, Restaurant restaurant)
            throws PermissionException, IdInvalidException;

    /**
     * Xóa đơn hàng — kiểm tra đơn hàng phải thuộc nhà hàng hiện tại.
     */
    void deleteOrderWithOwnershipCheck(Long id, Restaurant restaurant)
            throws PermissionException, IdInvalidException;

    /** Lấy danh sách đơn hàng của nhà hàng */
    List<ResOrder> getAllOrders(Restaurant restaurant);

    /** Chuyển đổi entity → response DTO */
    ResOrder toResOrder(Order order);
}
