package nhomlamdoan.smartrestaurant.domain.response.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nhomlamdoan.smartrestaurant.domain.constant.GenderEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private String phone;
    private GenderEnum gender;
    private String address;
    private int age;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private RoleUser role;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleUser {
        private long id;
        private String name;
    }
}
