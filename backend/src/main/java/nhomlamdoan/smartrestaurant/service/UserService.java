package nhomlamdoan.smartrestaurant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Role;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.response.user.ResCreateUserDTO;
import nhomlamdoan.smartrestaurant.domain.response.user.ResUpdateUserDTO;
import nhomlamdoan.smartrestaurant.domain.response.user.ResUserDTO;

@Service
public interface UserService {
    User handleCreateUser(User user);
    List<User> findAllUsers();
    List<User> findAllUsersByCurrentUserScope();
    User fetchUserById(Long id);
    void deleteUser(Long id);
    User handleUpdateUser(User user);
    User handleGetUserByUsername(String username);

    // Method mới — load kèm role + permissions, dùng trong PermissionInterceptor
    User handleGetUserByUsernameWithRole(String username);

    ResCreateUserDTO convertToResCreateUserDTO(User user);
    ResUpdateUserDTO convertToResUpdateUserDTO(User user);
    ResUserDTO convertToResUserDTO(User user);
    void updateUserToken(String token, String email);
    User getUserByRefreshTokenAndEmail(String refreshToken, String email);
    boolean isEmailExist(String email);
    Role findRoleByName(String name);
}