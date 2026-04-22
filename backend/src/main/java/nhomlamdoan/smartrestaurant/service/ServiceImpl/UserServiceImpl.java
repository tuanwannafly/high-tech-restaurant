package nhomlamdoan.smartrestaurant.service.ServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Role;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.response.user.ResCreateUserDTO;
import nhomlamdoan.smartrestaurant.domain.response.user.ResUpdateUserDTO;
import nhomlamdoan.smartrestaurant.domain.response.user.ResUserDTO;
import nhomlamdoan.smartrestaurant.repository.RoleRepository;
import nhomlamdoan.smartrestaurant.repository.UserRepository;
import nhomlamdoan.smartrestaurant.service.UserService;
import nhomlamdoan.smartrestaurant.util.SecurityUtil;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
    public List<User> findAllUsersByCurrentUserScope() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if (email.isEmpty()) {
            return List.of();
        }

        User currentUser = this.handleGetUserByUsernameWithRole(email);
        if (currentUser == null || currentUser.getRole() == null) {
            return List.of();
        }

        String roleName = currentUser.getRole().getName();
        if ("SUPER_ADMIN".equals(roleName)) {
            return this.userRepository.findAll();
        }

        if ("RESTAURANT_ADMIN".equals(roleName)) {
            if (currentUser.getRestaurant() == null || currentUser.getRestaurant().getRestaurantId() == null) {
                return List.of();
            }
            Long restaurantId = currentUser.getRestaurant().getRestaurantId();
            return this.userRepository.findAll().stream()
                    .filter(user -> user.getRestaurant() != null
                            && user.getRestaurant().getRestaurantId() != null
                            && restaurantId.equals(user.getRestaurant().getRestaurantId()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    @Override
    public User fetchUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        return userOptional.orElse(null);
    }

    @Override
    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setName(reqUser.getName());
            currentUser.setPhone(reqUser.getPhone());
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
            if (reqUser.getRole() != null && reqUser.getRole().getName() != null) {
                Role role = roleRepository.findByName(reqUser.getRole().getName());
                if (role != null) {
                    currentUser.setRole(role);
                }
            }
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    @Override
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    // ✅ Method mới — fetch user kèm role + permissions (dùng trong PermissionInterceptor)
    @Override
    public User handleGetUserByUsernameWithRole(String username) {
        return this.userRepository.findByEmailWithRoleAndPermissions(username);
    }

    @Override
    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    @Override
    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    @Override
    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public Role findRoleByName(String name) {
        return this.roleRepository.findByName(name);
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
        res.setPhone(user.getPhone());
        if (user.getRole() != null) {
            ResCreateUserDTO.RoleUser roleUser = new ResCreateUserDTO.RoleUser(
                user.getRole().getId(), user.getRole().getName());
            res.setRole(roleUser);
        }
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
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setPhone(user.getPhone());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        if (user.getRole() != null) {
            ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser(
                user.getRole().getId(), user.getRole().getName());
            res.setRole(roleUser);
        }
        return res;
    }
}