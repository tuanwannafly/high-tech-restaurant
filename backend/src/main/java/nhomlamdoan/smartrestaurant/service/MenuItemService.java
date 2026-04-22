package nhomlamdoan.smartrestaurant.service;

import java.util.List;

import nhomlamdoan.smartrestaurant.domain.MenuItem;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.request.menuitem.ReqMenuItem;
import nhomlamdoan.smartrestaurant.domain.response.menuitem.ResMenuItem;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;
import nhomlamdoan.smartrestaurant.util.error.PermissionException;

public interface MenuItemService {

    /** Tạo món ăn mới thuộc nhà hàng hiện tại */
    ResMenuItem createMenuItem(ReqMenuItem req, Restaurant restaurant);

    /**
     * Cập nhật món ăn — kiểm tra món phải thuộc nhà hàng của người dùng hiện tại.
     */
    ResMenuItem updateMenuItemWithOwnershipCheck(Long id, ReqMenuItem req, Restaurant restaurant)
            throws PermissionException, IdInvalidException;

    /**
     * Xóa món ăn — kiểm tra món phải thuộc nhà hàng của người dùng hiện tại.
     */
    void deleteMenuItemWithOwnershipCheck(Long id, Restaurant restaurant)
            throws PermissionException, IdInvalidException;

    /** Lấy danh sách món ăn của nhà hàng */
    List<ResMenuItem> getAllMenuItems(Restaurant restaurant);

    /** Chuyển đổi entity → response DTO */
    ResMenuItem toResMenuItem(MenuItem item);
}
