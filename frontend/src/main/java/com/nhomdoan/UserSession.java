package com.nhomdoan;

/**
 * Lưu thông tin user sau khi đăng nhập thành công (Singleton)
 */
public class UserSession {
    private static UserSession instance;

    private long id;
    private String name;
    private String email;
    private String accessToken;
    private String roleName;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public static void clear() { instance = null; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}
