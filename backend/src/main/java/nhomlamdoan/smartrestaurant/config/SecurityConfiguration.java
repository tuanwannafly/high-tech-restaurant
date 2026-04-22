package nhomlamdoan.smartrestaurant.config;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

@Configuration
@EnableMethodSecurity(securedEnabled = false)
public class SecurityConfiguration {
 
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
 
    @Value("${restaurant.jwt.base64-secret}")
    private String jwtKey;
 
    private final UserDetailsService userDetailsService;
 
    public SecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
    @Bean
    public JwtEncoder jwtEncoder() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }
 
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(JWT_ALGORITHM).build();
    }
 
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Spring Security 6: UserDetailsService được truyền qua constructor
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
 
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(java.util.Arrays.asList("*"));
        config.setAllowedMethods(java.util.Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setAllowedHeaders(java.util.Arrays.asList("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] whiteList = {
            "/",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/register",
            "/storage/**",
            "/api/v1/email/**",
            // Public user/resource endpoints (no token required)
            "/api/v1/users",
            "/api/v1/restaurants",
            "/api/v1/companies",
        };
 
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(whiteList).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
                // Trả về 401 JSON thay vì redirect khi token hết hạn/sai
                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            .authenticationProvider(authenticationProvider())
            .formLogin(f -> f.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
 
        return http.build();
    }
}