package nhomlamdoan.smartrestaurant.domain;
import jakarta.persistence.*;
import lombok.*;
import nhomlamdoan.smartrestaurant.domain.constant.CleaningStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "table_cleanings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableCleaning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cleaning_id")
    private Long cleaningId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CleaningStatus status;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;
}
