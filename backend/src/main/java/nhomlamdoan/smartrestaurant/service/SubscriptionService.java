package nhomlamdoan.smartrestaurant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.Subscription;

@Service
public interface SubscriptionService {
    Subscription handleCreateSubscription(Subscription subscription);
    // Role findRoleByName(String name);
    List<Subscription > findAllSubscriptions();
    Subscription fetchSubscriptionById(Long id);
    void deleteSubscription(Long id);
}
