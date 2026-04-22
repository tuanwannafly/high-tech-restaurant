package nhomlamdoan.smartrestaurant.service.ServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Permission;
import nhomlamdoan.smartrestaurant.domain.Role;
import nhomlamdoan.smartrestaurant.domain.response.ResultPaginationDTO;
import nhomlamdoan.smartrestaurant.repository.PermissionRepository;
import nhomlamdoan.smartrestaurant.repository.RoleRepository;
import nhomlamdoan.smartrestaurant.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    @Override
    public Role create(Role r) {
        // Resolve permissions từ DB theo danh sách id được gửi lên
        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions()
                    .stream().map(Permission::getId)
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(r);
    }

    @Override
    public Role fetchById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        return roleOptional.orElse(null);
    }

    @Override
    public Role update(Role r) {
        Role roleDB = this.fetchById(r.getId());
        if (roleDB == null) return null;

        // Resolve permissions từ DB
        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions()
                    .stream().map(Permission::getId)
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            roleDB.setPermissions(dbPermissions);
        }

        roleDB.setName(r.getName());
        roleDB.setDescription(r.getDescription());
        roleDB.setActive(r.isActive());
        return this.roleRepository.save(roleDB);
    }

    @Override
    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    @Override
    public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pRole.getTotalPages());
        mt.setTotal(pRole.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pRole.getContent());
        return rs;
    }

    @Override
    public Role findByName(String name) {
        return this.roleRepository.findByName(name);
    }
}
