package com.restaurant.data;

/**
 * Singleton lưu trữ JWT access token và thông tin user sau khi đăng nhập.
 */
public class AuthManager {

    private static AuthManager instance;

    private String accessToken;
    private long   userId;
    private String userName;
    private String userEmail;
    private String userRole;

    private AuthManager() {}

    public static AuthManager getInstance() {
        if (instance == null) instance = new AuthManager();
        return instance;
    }

    // ─── Token ───────────────────────────────────────────
    public String getAccessToken()  { return accessToken; }
    public void   setAccessToken(String t) { this.accessToken = t; }

    public boolean isLoggedIn() { return accessToken != null && !accessToken.isEmpty(); }

    public void logout() {
        accessToken = null;
        userId      = 0;
        userName    = null;
        userEmail   = null;
        userRole    = null;
    }

    // ─── User info ────────────────────────────────────────
    public long   getUserId()    { return userId; }
    public String getUserName()  { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getUserRole()  { return userRole; }

    public void setUserInfo(long id, String name, String email, String role) {
        this.userId    = id;
        this.userName  = name;
        this.userEmail = email;
        this.userRole  = role;
    }
}
