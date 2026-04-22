package nhomlamdoan.smartrestaurant.domain.response.user;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nhomlamdoan.smartrestaurant.domain.constant.GenderEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String email;
    private String name;
    private String phone;
    private GenderEnum gender;
    private String address;
    private int age;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleUser {
        private long id;
        private String name;
    }
    private RoleUser role;
}