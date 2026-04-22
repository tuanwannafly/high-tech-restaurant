package nhomlamdoan.smartrestaurant.repository;

import nhomlamdoan.smartrestaurant.domain.Menu;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByRestaurant(Restaurant restaurant);
    Optional<Menu> findByNameAndRestaurant(String name, Restaurant restaurant);
}
