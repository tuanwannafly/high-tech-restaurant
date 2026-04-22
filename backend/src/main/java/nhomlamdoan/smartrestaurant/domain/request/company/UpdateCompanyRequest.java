package nhomlamdoan.smartrestaurant.domain.request.company;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCompanyRequest {

    @NotNull(message = "Company ID không được để trống")
    private Long companyId;

    private String name;

    private String address;

    private String phone;

    private String email;

    private String logo;

    private String description;
}
