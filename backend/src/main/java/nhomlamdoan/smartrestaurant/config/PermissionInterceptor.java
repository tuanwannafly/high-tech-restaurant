package nhomlamdoan.smartrestaurant.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nhomlamdoan.smartrestaurant.domain.Permission;
import nhomlamdoan.smartrestaurant.domain.Role;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.service.UserService;
import nhomlamdoan.smartrestaurant.util.SecurityUtil;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email != null && !email.isEmpty()) {
            // ✅ Dùng query JOIN FETCH — load role + permissions trong 1 query, tránh LazyInitializationException
            User user = this.userService.handleGetUserByUsernameWithRole(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    // ✅ SUPER_ADMIN bypass hoàn toàn — không check permission
                    if ("SUPER_ADMIN".equals(role.getName())) {
                        return true;
                    }

                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions != null && permissions.stream().anyMatch(item ->
                            item.getApiPath().equals(path)
                            && item.getMethod().equals(httpMethod));

                    if (!isAllow) {
                        throw new IdInvalidException("Bạn không có quyền truy cập endpoint này.");
                    }
                } else {
                    throw new IdInvalidException("Bạn không có quyền truy cập endpoint này.");
                }
            }
        }

        return true;
    }
}