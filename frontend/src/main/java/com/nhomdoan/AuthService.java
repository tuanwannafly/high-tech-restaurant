package com.nhomdoan;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Service gọi API backend Spring Boot
 */
public class AuthService {

    private static final String BASE_URL = "http://localhost:8080/api/v1";
    private static final Gson gson = new Gson();
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // ─── DTO nội bộ ────────────────────────────────────────────────────────────

    public static class LoginResult {
        public boolean success;
        public String message;
        public UserSession session;
    }

    public static class RegisterResult {
        public boolean success;
        public String message;
    }

    // ─── Đăng nhập ─────────────────────────────────────────────────────────────

    public static LoginResult login(String email, String password) {
        LoginResult result = new LoginResult();
        try {
            JsonObject body = new JsonObject();
            body.addProperty("username", email);   // backend dùng username = email
            body.addProperty("password", password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                UserSession session = UserSession.getInstance();
                session.setAccessToken(json.get("access_token").getAsString());

                JsonObject user = json.getAsJsonObject("user");
                session.setId(user.get("id").getAsLong());
                session.setEmail(user.get("email").getAsString());
                session.setName(user.get("name").getAsString());
                if (user.has("role") && !user.get("role").isJsonNull()) {
                    session.setRoleName(user.getAsJsonObject("role").get("name").getAsString());
                }

                result.success = true;
                result.session = session;
            } else {
                JsonObject err = gson.fromJson(response.body(), JsonObject.class);
                result.success = false;
                result.message = err.has("message") ? err.get("message").getAsString()
                        : "Sai email hoặc mật khẩu.";
            }
        } catch (Exception e) {
            result.success = false;
            result.message = "Không thể kết nối tới server. Vui lòng thử lại.";
        }
        return result;
    }

    // ─── Đăng ký ───────────────────────────────────────────────────────────────

    public static RegisterResult register(String name, String email, String password,
                                          String phone, int age) {
        RegisterResult result = new RegisterResult();
        try {
            JsonObject body = new JsonObject();
            body.addProperty("name", name);
            body.addProperty("email", email);
            body.addProperty("password", password);
            body.addProperty("phone", phone);
            body.addProperty("age", age);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                result.success = true;
                result.message = "Đăng ký thành công!";
            } else {
                JsonObject err = gson.fromJson(response.body(), JsonObject.class);
                result.success = false;
                result.message = err.has("message") ? err.get("message").getAsString()
                        : "Đăng ký thất bại.";
            }
        } catch (Exception e) {
            result.success = false;
            result.message = "Không thể kết nối tới server. Vui lòng thử lại.";
        }
        return result;
    }
}
