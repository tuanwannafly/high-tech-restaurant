package nhomlamdoan.smartrestaurant.service.ServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.response.user.ResCreateUserDTO;
import nhomlamdoan.smartrestaurant.domain.response.user.ResUpdateUserDTO;
import nhomlamdoan.smartrestaurant.domain.response.user.ResUserDTO;
import nhomlamdoan.smartrestaurant.repository.UserRepository;
import nhomlamdoan.smartrestaurant.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User fetchUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
            if (currentUser != null) {
                    currentUser.setAddress(reqUser.getAddress());
                    currentUser.setGender(reqUser.getGender());
                    currentUser.setAge(reqUser.getAge());
                    currentUser.setName(reqUser.getName());
            }
            currentUser = this.userRepository.save(currentUser);
        
        return currentUser;
    }

    @Override
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    @Override
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        return res;
    }

    @Override
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    @Override
    public ResUserDTO convertToResUserDTO(User user) {
             ResUserDTO res = new ResUserDTO();
        // ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();
        // ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();

        // if (user.getCompany() != null) {
        // //     com.setId(user.getCompany().getId());
        // //     com.setName(user.getCompany().getName());
        // //     res.setCompany(com);
        // // }

        // if (user.getRole() != null) {
        //     roleUser.setId(user.getRole().getId());
        //     roleUser.setName(user.getRole().getName());
        //     res.setRole(roleUser);
        // }

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    @Override
    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }
    
}
