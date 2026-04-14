package nhomlamdoan.smartrestaurant.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.Role;
import nhomlamdoan.smartrestaurant.domain.response.ResultPaginationDTO;

@Service
public interface RoleService {
    Role handleCreateRole(Role role);
    // Role findRoleByName(String name);
    List<Role> findAllRoles();
    Role fetchRoleById(Long id);
    void deleteRole(Long id);
    ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable);
    Role findByName(String name);
}
