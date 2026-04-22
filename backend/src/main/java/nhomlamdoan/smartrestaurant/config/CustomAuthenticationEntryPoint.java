package nhomlamdoan.smartrestaurant.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");

        String errorMessage = Optional.ofNullable(authException.getCause())
                .map(Throwable::getMessage)
                .orElse(authException.getMessage());

        // Escape nội dung để tránh lỗi JSON
        String safeError   = errorMessage   != null ? errorMessage.replace("\"", "'")   : "Unauthorized";
        String safeMessage = "Token không hợp lệ (hết hạn, không đúng định dạng, hoặc không truyền JWT ở header)...";

        String json = "{"
                + "\"statusCode\":" + HttpStatus.UNAUTHORIZED.value() + ","
                + "\"error\":\"" + safeError + "\","
                + "\"message\":\"" + safeMessage + "\","
                + "\"data\":null"
                + "}";

        response.getWriter().write(json);
    }
}
