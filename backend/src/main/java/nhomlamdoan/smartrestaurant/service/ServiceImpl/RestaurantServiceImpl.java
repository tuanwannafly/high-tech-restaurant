package nhomlamdoan.smartrestaurant.service.ServiceImpl;
import java.time.LocalDateTime;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Company;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.request.restaurant.ReqRestaurant;
import nhomlamdoan.smartrestaurant.domain.response.Restaurant.ResRestaurant;
import nhomlamdoan.smartrestaurant.repository.CompanyRepository;
import nhomlamdoan.smartrestaurant.repository.RestaurantRepository;
import nhomlamdoan.smartrestaurant.service.RestaurantService;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CompanyRepository companyRepository;

    

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public ResRestaurant handleCreateRestaurant(ReqRestaurant req) {
        // 1. Tìm Company
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // 2. Map từ Request sang Entity
        Restaurant restaurant = Restaurant.builder()
                .name(req.getName())
                .address(req.getAddress())
                .phone(req.getPhone())
                .status(req.getStatus())
                .createdAt(LocalDateTime.now())
                .company(company)
                .build();

        Restaurant savedRes = restaurantRepository.save(restaurant);

        // 3. Map từ Entity sang Response
        return ResRestaurant.builder()
                .restaurantId(savedRes.getRestaurantId())
                .name(savedRes.getName())
                .address(savedRes.getAddress())
                .phone(savedRes.getPhone())
                .status(savedRes.getStatus())
                .createdAt(savedRes.getCreatedAt())
                .company(ResRestaurant.CompanySummary.builder()
                        .companyId(company.getCompanyId())
                        .name(company.getName())
                        .build())
                .build();
    }

    @Override
    public List<Restaurant> findAllRestaurants() {
        return this.restaurantRepository.findAll();
    }

    @Override
    public Restaurant fetchRestaurantById(Long id) {
        Optional<Restaurant> userOptional = this.restaurantRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    @Override
    public void deleteRestaurant(Long id) {
        this.restaurantRepository.deleteById(id);
    }
    
}