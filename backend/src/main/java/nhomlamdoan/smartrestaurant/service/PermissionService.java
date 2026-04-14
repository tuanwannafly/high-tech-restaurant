package nhomlamdoan.smartrestaurant.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Permission;
import nhomlamdoan.smartrestaurant.domain.response.ResultPaginationDTO;

@Service
public interface PermissionService {
    boolean isPermissionExist(Permission permission);
    Permission fetchById(long id);
    Permission create(Permission p);
    Permission update(Permission p);
    void delete(long id);
    ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable);
    boolean isSameName(Permission permission);
}
