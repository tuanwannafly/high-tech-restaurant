package nhomlamdoan.smartrestaurant.domain.request.restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import nhomlamdoan.smartrestaurant.domain.constant.RestaurantStatus;

@Getter
@Setter
public class ReqRestaurant {

    @NotBlank(message = "Tên nhà hàng không được để trống")
    private String name;

    private String address;

    private String phone;

    private RestaurantStatus status; 

    @NotNull(message = "Company ID không được để trống")
    private Long companyId;
}