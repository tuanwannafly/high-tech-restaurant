package nhomlamdoan.smartrestaurant.domain.response.user;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nhomlamdoan.smartrestaurant.domain.constant.GenderEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
}
