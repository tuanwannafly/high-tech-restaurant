package nhomlamdoan.smartrestaurant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.request.restaurant.ReqRestaurant;
import nhomlamdoan.smartrestaurant.domain.response.Restaurant.ResRestaurant;

@Service
public interface RestaurantService {
    ResRestaurant handleCreateRestaurant(ReqRestaurant restaurant);
    // Role findRoleByName(String name);
    List<Restaurant> findAllRestaurants();
    Restaurant fetchRestaurantById(Long id);
    void deleteRestaurant(Long id);
}
