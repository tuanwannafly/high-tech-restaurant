package nhomlamdoan.smartrestaurant.domain.request.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqOrder {
    private Long tableId;
    // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String status;
    private List<ReqOrderItem> items;

    @Getter
    @Setter
    public static class ReqOrderItem {
        private Long menuItemId;
        private Integer quantity;
        private String note;
    }
}
