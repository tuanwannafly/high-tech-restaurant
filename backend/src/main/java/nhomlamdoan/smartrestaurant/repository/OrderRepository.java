package nhomlamdoan.smartrestaurant.repository;

import nhomlamdoan.smartrestaurant.domain.Order;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRestaurant(Restaurant restaurant);
}
