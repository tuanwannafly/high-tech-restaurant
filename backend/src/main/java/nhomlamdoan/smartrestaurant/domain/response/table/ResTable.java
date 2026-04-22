package nhomlamdoan.smartrestaurant.domain.response.table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResTable {
    private Long id;
    private String tableNumber;
    private Integer capacity;
    private String status;
}
