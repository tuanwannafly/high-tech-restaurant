package nhomlamdoan.smartrestaurant.controller;

import java.util.List;

import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.request.menuitem.ReqMenuItem;
import nhomlamdoan.smartrestaurant.domain.response.menuitem.ResMenuItem;
import nhomlamdoan.smartrestaurant.service.MenuItemService;
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
@RequestMapping("/api/v1/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final UserService userService;

    public MenuItemController(MenuItemService menuItemService, UserService userService) {
        this.menuItemService = menuItemService;
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
     * GET /api/v1/menu-items
     * Tất cả nhân viên đều được xem thực đơn (để phục vụ gọi món).
     */
    @GetMapping
    @ApiMessage("Lấy danh sách món ăn")
    public ResponseEntity<List<ResMenuItem>> getAll() throws IdInvalidException {
        return ResponseEntity.ok(menuItemService.getAllMenuItems(getCurrentRestaurant()));
    }

    /**
     * POST /api/v1/menu-items
     * Chỉ RESTAURANT_ADMIN mới được thêm món mới vào thực đơn.
     */
    @PostMapping
    @ApiMessage("Thêm món ăn mới")
    public ResponseEntity<ResMenuItem> create(@RequestBody ReqMenuItem req)
            throws IdInvalidException, PermissionException {

        User user = getCurrentUser();
        if (!RoleConstants.RESTAURANT_ADMIN.equals(getRoleName(user))) {
            throw new PermissionException("Chỉ RESTAURANT_ADMIN mới có thể thêm món ăn.");
        }

        ResMenuItem res = menuItemService.createMenuItem(req, getCurrentRestaurant());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    /**
     * PUT /api/v1/menu-items/{id}
     * Chỉ RESTAURANT_ADMIN mới được chỉnh sửa thông tin món ăn.
     * Việc cập nhật xác minh món thuộc nhà hàng hiện tại.
     */
    @PutMapping("/{id}")
    @ApiMessage("Cập nhật món ăn")
    public ResponseEntity<ResMenuItem> update(@PathVariable Long id,
                                               @RequestBody ReqMenuItem req)
            throws IdInvalidException, PermissionException {

        User user = getCurrentUser();
        if (!RoleConstants.RESTAURANT_ADMIN.equals(getRoleName(user))) {
            throw new PermissionException("Chỉ RESTAURANT_ADMIN mới có thể cập nhật món ăn.");
        }

        ResMenuItem res = menuItemService.updateMenuItemWithOwnershipCheck(id, req, getCurrentRestaurant());
        return ResponseEntity.ok(res);
    }

    /**
     * DELETE /api/v1/menu-items/{id}
     * Chỉ RESTAURANT_ADMIN mới được xóa món khỏi thực đơn.
     */
    @DeleteMapping("/{id}")
    @ApiMessage("Xóa món ăn")
    public ResponseEntity<Void> delete(@PathVariable Long id)
            throws IdInvalidException, PermissionException {

        User user = getCurrentUser();
        if (!RoleConstants.RESTAURANT_ADMIN.equals(getRoleName(user))) {
            throw new PermissionException("Chỉ RESTAURANT_ADMIN mới có thể xóa món ăn.");
        }

        menuItemService.deleteMenuItemWithOwnershipCheck(id, getCurrentRestaurant());
        return ResponseEntity.ok(null);
    }
}
