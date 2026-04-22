package nhomlamdoan.smartrestaurant.service;

import java.util.List;

import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.RestaurantTable;
import nhomlamdoan.smartrestaurant.domain.request.table.ReqTable;
import nhomlamdoan.smartrestaurant.domain.response.table.ResTable;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;
import nhomlamdoan.smartrestaurant.util.error.PermissionException;

public interface TableService {

    /** Tạo bàn mới thuộc nhà hàng hiện tại */
    ResTable createTable(ReqTable req, Restaurant restaurant);

    /**
     * Cập nhật bàn — kiểm tra bàn phải thuộc nhà hàng của người dùng hiện tại.
     * Kiểm tra quyền theo role được thực hiện ở tầng Controller.
     */
    ResTable updateTableWithOwnershipCheck(Long id, ReqTable req, Restaurant restaurant)
            throws PermissionException, IdInvalidException;

    /**
     * Xóa bàn — kiểm tra bàn phải thuộc nhà hàng của người dùng hiện tại.
     */
    void deleteTableWithOwnershipCheck(Long id, Restaurant restaurant)
            throws PermissionException, IdInvalidException;

    /** Lấy danh sách bàn của nhà hàng */
    List<ResTable> getAllTables(Restaurant restaurant);

    /** Chuyển đổi entity → response DTO */
    ResTable toResTable(RestaurantTable table);
}
