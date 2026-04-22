package nhomlamdoan.smartrestaurant.domain.request.menuitem;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReqMenuItem {
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    // category maps to Menu name
    private String category;
}
