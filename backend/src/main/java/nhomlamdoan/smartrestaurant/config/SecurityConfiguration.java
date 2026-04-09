package nhomlamdoan.smartrestaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = false)
public class SecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    String[] whiteList = {
        "/",
        "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/register",
        "/storage/**",
        "/api/v1/email/**",
        "/api/v1/roles",
        "/api/v1/users" // Thêm dòng này để cho phép POST/PUT/DELETE thoải mái khi test
        };
    http
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(authz -> authz
                .requestMatchers(whiteList).permitAll()
                .anyRequest().permitAll()
        )
        // Comment đoạn này lại nếu chưa dùng đến JWT
        // .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())) 
        .formLogin(f -> f.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    
    return http.build();
}

}
