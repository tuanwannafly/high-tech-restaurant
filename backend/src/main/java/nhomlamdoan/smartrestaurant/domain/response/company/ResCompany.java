package nhomlamdoan.smartrestaurant.domain.response.company;

import lombok.Getter;
import lombok.Setter;
import nhomlamdoan.smartrestaurant.domain.constant.CompanyStatus;

@Getter
@Setter
public class ResCompany {
    private Long companyId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String logo;
    private String description;
    private CompanyStatus status;
    private UserResponse user;

    @Getter
    @Setter
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
    }
}
