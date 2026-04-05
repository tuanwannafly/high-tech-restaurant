package nhomlamdoan.smartrestaurant.domain;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import nhomlamdoan.smartrestaurant.domain.constant.KitchenStatus;

@Entity
@Table(name = "kitchen_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private KitchenStatus status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "finish_time")
    private LocalDateTime finishTime;

    // Relations
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false, unique = true)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chef_id", nullable = false)
    private User chef;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = KitchenStatus.PENDING;
        }
    }
}