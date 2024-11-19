package com.example.glass_project.data.model.user;

// Lớp LoginResponse để chứa thông tin người dùng và token
public class LoginResponse {
    private UserAccount user; // Đổi User thành UserAccount
    private String accessToken;
    private String refreshToken;

    public LoginResponse(UserAccount user, String accessToken, String refreshToken) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
