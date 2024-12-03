package com.example.glass_project.data.model.order;

import java.util.List;

public class Shipper {
    private int id;
    private String username;
    private String email;
    private boolean status;
    private int roleID;
    private String phoneNumber;
    private List<Profile> profiles;

    public Shipper(int id, String username, String email, boolean status, int roleID, String phoneNumber, List<Profile> profiles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.status = status;
        this.roleID = roleID;
        this.phoneNumber = phoneNumber;
        this.profiles = profiles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
}
