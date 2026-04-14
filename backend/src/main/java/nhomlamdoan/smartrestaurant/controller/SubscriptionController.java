package nhomlamdoan.smartrestaurant.controller;

import java.util.concurrent.Flow;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.Subscription;
import nhomlamdoan.smartrestaurant.service.SubscriptionService;

@RestController
@RequestMapping("/api/v1")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/subscriptions")
    //@ApiMessage("Create a new user")
    public ResponseEntity<Subscription> createSubscription(@Valid @RequestBody Subscription sub) {
        // if (this.userService.isEmailExist(user.getEmail())) {
        //     throw new IdInvalidException(
        //             "Email " + user.getEmail() + " đã tồn tại, vui lòng sử dụng email khác.");
        // }
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        Subscription newSub = this.subscriptionService.handleCreateSubscription(sub);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newSub);
    }


}
