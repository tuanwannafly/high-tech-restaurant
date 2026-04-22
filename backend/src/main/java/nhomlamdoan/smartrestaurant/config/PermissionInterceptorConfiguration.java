package nhomlamdoan.smartrestaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/",
                "/api/v1/auth/**",        // login, logout, refresh — không cần check permission
                "/storage/**",
                "/api/v1/companies/**",
                "/api/v1/restaurants/**",
                "/api/v1/roles/**",
                "/api/v1/permissions/**"  // SUPER_ADMIN setup — bypass qua role check trong interceptor
                // ❌ KHÔNG whitelist /tables, /orders, /menu-items
                // → PermissionInterceptor phải kiểm tra quyền cho các endpoint này
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}