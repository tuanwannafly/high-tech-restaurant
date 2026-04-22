package nhomlamdoan.smartrestaurant.repository;

import nhomlamdoan.smartrestaurant.domain.MenuItem;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    @Query("SELECT mi FROM MenuItem mi JOIN mi.menu m WHERE m.restaurant = :restaurant")
    List<MenuItem> findByMenuRestaurant(@Param("restaurant") Restaurant restaurant);
}
