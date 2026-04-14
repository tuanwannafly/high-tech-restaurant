package nhomlamdoan.smartrestaurant.service.ServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Subscription;
import nhomlamdoan.smartrestaurant.repository.SubscriptionRepository;
import nhomlamdoan.smartrestaurant.service.SubscriptionService;

@Service
public class SubscriptionServiceImpl implements SubscriptionService{

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }



    @Override
    public Subscription handleCreateSubscription(Subscription subscription) {
        return this.subscriptionRepository.save(subscription);
    }

    @Override
    public List<Subscription> findAllSubscriptions() {
        return this.subscriptionRepository.findAll();
    }

    @Override
    public Subscription fetchSubscriptionById(Long id) {
        Optional<Subscription> subOptional = this.subscriptionRepository.findById(id);
        if (subOptional.isPresent()) {
            return subOptional.get();
        }
        return null;
    }

    @Override
    public void deleteSubscription(Long id) {
        this.subscriptionRepository.deleteById(id);
    }
    
}
