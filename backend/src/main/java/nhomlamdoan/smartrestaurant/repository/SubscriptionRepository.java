package nhomlamdoan.smartrestaurant.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nhomlamdoan.smartrestaurant.domain.Subscription;

@Repository
public interface SubscriptionRepository extends  JpaRepository<Subscription, Long>{
    
}
