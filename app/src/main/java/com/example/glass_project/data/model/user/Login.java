package com.example.glass_project.data.model.user;

import java.util.List;

public class Login {
    private String username;
    private String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

