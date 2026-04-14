package nhomlamdoan.smartrestaurant.domain.response.Restaurant;
import java.time.LocalDateTime;



import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nhomlamdoan.smartrestaurant.domain.constant.RestaurantStatus;

@Getter
@Setter
@Builder
public class ResRestaurant {
    private Long restaurantId;
    private String name;
    private String address;
    private String phone;
    private RestaurantStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Chỉ trả về thông tin cơ bản của Company
    private CompanySummary company;

    @Getter
    @Setter
    @Builder
    public static class CompanySummary {
        private Long companyId;
        private String name;
    }
}