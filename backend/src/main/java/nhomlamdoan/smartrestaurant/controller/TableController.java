package nhomlamdoan.smartrestaurant.controller;

import java.util.List;

import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.request.table.ReqTable;
import nhomlamdoan.smartrestaurant.domain.response.table.ResTable;
import nhomlamdoan.smartrestaurant.service.TableService;
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
@RequestMapping("/api/v1/tables")
public class TableController {

    private final TableService tableService;
    private final UserService userService;

    public TableController(TableService tableService, UserService userService) {
        this.tableService = tableService;
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
     * GET /api/v1/tables
     * Tất cả nhân viên thuộc nhà hàng đều có thể xem danh sách bàn.
     */
    @GetMapping
    @ApiMessage("Lấy danh sách bàn")
    public ResponseEntity<List<ResTable>> getAll() throws IdInvalidException {
        return ResponseEntity.ok(tableService.getAllTables(getCurrentRestaurant()));
    }

    /**
     * POST /api/v1/tables
     * Chỉ RESTAURANT_ADMIN mới được thêm bàn mới.
     */
    @PostMapping
    @ApiMessage("Thêm bàn mới")
    public ResponseEntity<ResTable> create(@RequestBody ReqTable req)
            throws IdInvalidException, PermissionException {

        User user = getCurrentUser();
        if (!RoleConstants.RESTAURANT_ADMIN.equals(getRoleName(user))) {
            throw new PermissionException("Chỉ RESTAURANT_ADMIN mới có thể thêm bàn.");
        }

        ResTable res = tableService.createTable(req, getCurrentRestaurant());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    /**
     * PUT /api/v1/tables/{id}
     * - RESTAURANT_ADMIN : toàn quyền (đổi số bàn, sức chứa, trạng thái)
     * - WAITER / CASHIER : chỉ được đổi trạng thái bàn (status)
     */
    @PutMapping("/{id}")
    @ApiMessage("Cập nhật bàn")
    public ResponseEntity<ResTable> update(@PathVariable Long id,
                                           @RequestBody ReqTable req)
            throws IdInvalidException, PermissionException {

        User user = getCurrentUser();
        String role = getRoleName(user);

        boolean isAdmin   = RoleConstants.RESTAURANT_ADMIN.equals(role);
        boolean isWaiter  = RoleConstants.WAITER.equals(role);
        boolean isCashier = RoleConstants.CASHIER.equals(role);

        if (!isAdmin && !isWaiter && !isCashier) {
            throw new PermissionException("Bạn không có quyền cập nhật bàn.");
        }

        // WAITER / CASHIER không được thay đổi thông tin vật lý của bàn
        if ((isWaiter || isCashier) && (req.getTableNumber() != null || req.getCapacity() != null)) {
            throw new PermissionException("Bạn chỉ được phép thay đổi trạng thái bàn.");
        }

        ResTable res = tableService.updateTableWithOwnershipCheck(id, req, getCurrentRestaurant());
        return ResponseEntity.ok(res);
    }

    /**
     * DELETE /api/v1/tables/{id}
     * Chỉ RESTAURANT_ADMIN mới được xóa bàn.
     */
    @DeleteMapping("/{id}")
    @ApiMessage("Xóa bàn")
    public ResponseEntity<Void> delete(@PathVariable Long id)
            throws IdInvalidException, PermissionException {

        User user = getCurrentUser();
        if (!RoleConstants.RESTAURANT_ADMIN.equals(getRoleName(user))) {
            throw new PermissionException("Chỉ RESTAURANT_ADMIN mới có thể xóa bàn.");
        }

        tableService.deleteTableWithOwnershipCheck(id, getCurrentRestaurant());
        return ResponseEntity.ok(null);
    }
}
