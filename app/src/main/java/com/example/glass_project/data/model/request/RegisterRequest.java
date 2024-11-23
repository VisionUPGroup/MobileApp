package com.example.glass_project.data.model.request;


public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    public RegisterRequest(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    // Getter and Setter methods if needed
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
}


