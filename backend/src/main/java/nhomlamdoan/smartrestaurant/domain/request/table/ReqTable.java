package nhomlamdoan.smartrestaurant.domain.request.table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqTable {
    private String tableNumber;
    private Integer capacity;
    // AVAILABLE, OCCUPIED, RESERVED
    private String status;
}
