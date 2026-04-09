package nhomlamdoan.smartrestaurant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.response.user.ResCreateUserDTO;
import nhomlamdoan.smartrestaurant.domain.response.user.ResUpdateUserDTO;
import nhomlamdoan.smartrestaurant.domain.response.user.ResUserDTO;

@Service
public interface  UserService {
    User handleCreateUser(User user);
    // Role findRoleByName(String name);
    List<User> findAllUsers();
    User fetchUserById(Long id);
    void deleteUser(Long id);
    User handleUpdateUser( User user);
    User handleGetUserByUsername(String username);
    ResCreateUserDTO convertToResCreateUserDTO(User user);
    ResUpdateUserDTO convertToResUpdateUserDTO(User user);
    ResUserDTO convertToResUserDTO(User user);
}
