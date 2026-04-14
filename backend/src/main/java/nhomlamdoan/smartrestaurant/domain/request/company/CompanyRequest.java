package nhomlamdoan.smartrestaurant.domain.request.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequest {

    @NotBlank(message = "Tên công ty không được để trống")
    private String name;

    private String address;

    private String phone;

    @Email(message = "Email không hợp lệ")
    private String email;

    @NotNull(message = "User ID không được để trống")
    private Long userId; // Chỉ truyền ID của User ở đây
}