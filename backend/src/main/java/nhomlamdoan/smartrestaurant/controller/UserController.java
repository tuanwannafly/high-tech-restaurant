package nhomlamdoan.smartrestaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import jakarta.validation.Valid;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.Role;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.response.user.ResCreateUserDTO;
import nhomlamdoan.smartrestaurant.domain.response.user.ResUpdateUserDTO;
import nhomlamdoan.smartrestaurant.domain.response.user.ResUserDTO;
import nhomlamdoan.smartrestaurant.service.RestaurantService;
import nhomlamdoan.smartrestaurant.service.UserService;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RestaurantService restaurantService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder,
            RestaurantService restaurantService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResUserDTO>> getAllUsers() {
        List<ResUserDTO> users = this.userService.findAllUsersByCurrentUserScope()
                .stream()
                .map(this.userService::convertToResUserDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Resolve Role từ DB theo name
        if (user.getRole() != null && user.getRole().getName() != null) {
            Role role = this.userService.findRoleByName(user.getRole().getName());
            user.setRole(role);
        }
        // Resolve Restaurant từ DB theo restaurantId
        if (user.getRestaurant() != null && user.getRestaurant().getRestaurantId() != null) {
            Restaurant restaurant = this.restaurantService.fetchRestaurantById(
                user.getRestaurant().getRestaurantId());
            user.setRestaurant(restaurant);
        }
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable("id") Long id)
            throws IdInvalidException {
        User user = this.userService.fetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id)
            throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@Valid @RequestBody User user) {
        User updated = this.userService.handleUpdateUser(user);
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(updated));
    }
}