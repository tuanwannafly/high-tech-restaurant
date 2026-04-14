package nhomlamdoan.smartrestaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import nhomlamdoan.smartrestaurant.domain.Role;
import nhomlamdoan.smartrestaurant.service.RoleService;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    //@ApiMessage("Create a new user")
    public ResponseEntity<Role> createUser(@Valid @RequestBody Role role) {
        // if (this.userService.isEmailExist(user.getEmail())) {
        //     throw new IdInvalidException(
        //             "Email " + user.getEmail() + " đã tồn tại, vui lòng sử dụng email khác.");
        // }
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role newRole = this.roleService.handleCreateRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newRole);
    }

    
}
