package nhomlamdoan.smartrestaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nhomlamdoan.smartrestaurant.domain.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant , Long>{


}
