package nhomlamdoan.smartrestaurant.controller;

import nhomlamdoan.smartrestaurant.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.request.restaurant.ReqRestaurant;
import nhomlamdoan.smartrestaurant.domain.response.Restaurant.ResRestaurant;
import nhomlamdoan.smartrestaurant.service.RestaurantService;

@RestController
@RequestMapping("/api/v1")
public class RestaurantController {
    private final CompanyService companyService;
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService, CompanyService companyService) {
        this.restaurantService = restaurantService;
        this.companyService = companyService;
    }

    @PostMapping("/restaurants")
    //@ApiMessage("Create a new user")
    public ResponseEntity<ResRestaurant> createUser(@Valid @RequestBody ReqRestaurant restaurant) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.restaurantService.handleCreateRestaurant(restaurant));
    }

}
