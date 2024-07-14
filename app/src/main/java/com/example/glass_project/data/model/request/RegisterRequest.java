package com.example.glass_project.data.model.request;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

}
