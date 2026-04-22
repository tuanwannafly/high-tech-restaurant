package nhomlamdoan.smartrestaurant.domain.response.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ResOrder {
    private Long id;
    private String tableNumber;
    private Long tableId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private List<ResOrderItem> items;

    @Getter
    @Setter
    public static class ResOrderItem {
        private Long orderItemId;
        private Long menuItemId;
        private String menuItemName;
        private Integer quantity;
        private BigDecimal price;
        private String note;
        private String status;
    }
}
