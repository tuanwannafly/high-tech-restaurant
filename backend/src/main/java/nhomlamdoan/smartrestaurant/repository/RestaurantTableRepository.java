package nhomlamdoan.smartrestaurant.repository;

import nhomlamdoan.smartrestaurant.domain.RestaurantTable;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByRestaurant(Restaurant restaurant);
}
