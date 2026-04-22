package nhomlamdoan.smartrestaurant.repository;

import java.util.List;

import nhomlamdoan.smartrestaurant.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nhomlamdoan.smartrestaurant.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Query cũ — giữ nguyên cho các chỗ khác
    User findByEmail(String email);

    // Query mới dùng trong PermissionInterceptor — EAGER load role + permissions
    @Query("SELECT u FROM User u " +
           "LEFT JOIN FETCH u.role r " +
           "LEFT JOIN FETCH r.permissions " +
           "WHERE u.email = :email")
    User findByEmailWithRoleAndPermissions(@Param("email") String email);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String refreshToken, String email);

    List<User> findByCompany(Company company);
}