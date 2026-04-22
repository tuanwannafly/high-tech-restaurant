package nhomlamdoan.smartrestaurant.controller;

import java.util.List;

import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.request.order.ReqOrder;
import nhomlamdoan.smartrestaurant.domain.response.order.ResOrder;
import nhomlamdoan.smartrestaurant.service.OrderService;
import nhomlamdoan.smartrestaurant.service.UserService;
import nhomlamdoan.smartrestaurant.util.RoleConstants;
import nhomlamdoan.smartrestaurant.util.SecurityUtil;
import nhomlamdoan.smartrestaurant.util.annotation.ApiMessage;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;
import nhomlamdoan.smartrestaurant.util.error.PermissionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // ------------------------------------------------------------------ helpers

    private User getCurrentUser() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy thông tin đăng nhập."));
        User user = userService.handleGetUserByUsername(email);
        if (user == null) throw new IdInvalidException("Người dùng không tồn tại.");
        return user;
    }

    private Restaurant getCurrentRestaurant() throws IdInvalidException {
        User user = getCurrentUser();
        if (user.getRestaurant() == null)
            throw new IdInvalidException("Người dùng chưa được gán nhà hàng.");
        return user.getRestaurant();
    }

    private String getRoleName(User user) {
        return (user.getRole() != null) ? user.getRole().getName() : "";
    }

    // ------------------------------------------------------------------ endpoints

    /**
     * GET /api/v1/orders
     * RESTAURANT_ADMIN, WAITER, CASHIER, CHEF đều có thể xem đơn hàng
     * (chỉ đơn hàng thuộc nhà hàng của họ).
     */
    @GetMapping
    @ApiMessage("Lấy danh sách đơn hàng")
    public ResponseEntity<List<ResOrder>> getAll() throws IdInvalidException {
        return ResponseEntity.ok(orderService.getAllOrders(getCurrentRestaurant()));
    }

    /**
     * POST /api/v1/orders
     * Chỉ WAITER (và RESTAURANT_ADMIN) mới được tạo đơn hàng mới.
     */
    @PostMapping
    @ApiMessage("Tạo đơn hàng mới")
    public ResponseEntity<ResOrder> create(@RequestBody ReqOrder req)
            throws IdInvalidException, PermissionException {

        User user = getCurrentUser();
        String role = getRoleName(user);

        if (!RoleConstants.WAITER.equals(role) && !RoleConstants.RESTAURANT_ADMIN.equals(role)) {
            throw new PermissionException("Chỉ WAITER hoặc RESTAURANT_ADMIN mới có thể tạo đơn hàng.");
        }

        ResOrder res = orderService.createOrder(req, getCurrentRestaurant(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    /**
     * PUT /api/v1/orders/{id}
     * - RESTAURANT_ADMIN : toàn quyền cập nhật
     * - WAITER           : chỉ được thêm/sửa món (items), không được đổi status
     * - CASHIER          : chỉ được đổi status đơn hàng (COMPLETED / CANCELLED)
     * - CHEF             : chỉ được đổi status từng order item (qua OrderItemController sau)
     */
    @PutMapping("/{id}")
    @ApiMessage("Cập nhật đơn hàng")
    public ResponseEntity<ResOrder> update(@PathVariable Long id,
                                           @RequestBody ReqOrder req)
            throws IdInvalidException, PermissionException {

        User user = getCurrentUser();
        String role = getRoleName(user);

        boolean isAdmin   = RoleConstants.RESTAURANT_ADMIN.equals(role);
        boolean isWaiter  = RoleConstants.WAITER.equals(role);
        boolean isCashier = RoleConstants.CASHIER.equals(role);

        if (!isAdmin && !isWaiter && !isCashier) {
            throw new PermissionException("Bạn không có quyền cập nhật đơn hàng.");
        }

        // CASHIER chỉ được thay đổi status — không được sửa danh sách món
        if (isCashier && req.getItems() != null && !req.getItems().isEmpty()) {
            throw new PermissionException("CASHIER chỉ được phép thay đổi trạng thái đơn hàng.");
        }

        // WAITER không được thay đổi status chính của đơn
        if (isWaiter && req.getStatus() != null) {
            throw new PermissionException("WAITER không được phép thay đổi trạng thái đơn hàng.");
        }

        ResOrder res = orderService.updateOrderWithOwnershipCheck(id, req, getCurrentRestaurant());
        return ResponseEntity.ok(res);
    }

    /**
     * DELETE /api/v1/orders/{id}
     * Chỉ RESTAURANT_ADMIN mới được xóa đơn hàng.
     */
    @DeleteMapping("/{id}")
    @ApiMessage("Xóa đơn hàng")
    public ResponseEntity<Void> delete(@PathVariable Long id)
            throws IdInvalidException, PermissionException {

        User user = getCurrentUser();
        if (!RoleConstants.RESTAURANT_ADMIN.equals(getRoleName(user))) {
            throw new PermissionException("Chỉ RESTAURANT_ADMIN mới có thể xóa đơn hàng.");
        }

        orderService.deleteOrderWithOwnershipCheck(id, getCurrentRestaurant());
        return ResponseEntity.ok(null);
    }
}
