package com.example.glass_project.data.model.user;

import java.util.List;

// Lớp dùng để chứa thông tin người dùng
public class UserAccount {
    private int id;
    private String username;
    private String password;
    private String email;
    private  String phoneNumber;
    private boolean status;
    private int roleID;
    private Object role; // Bạn có thể thay đổi kiểu này nếu có thông tin về role
    private List<Object> profiles;
    private List<Object> orders;
    private List<Object> payments;
    private List<Object> ratingEyeGlasses;
    private List<Object> ratingLens;

    // Constructor
    public UserAccount(int id, String username, String password, String email, boolean status, int roleID, Object role, List<Object> profiles, List<Object> orders, List<Object> payments, List<Object> ratingEyeGlasses, List<Object> ratingLens) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.status = status;
        this.roleID = roleID;
        this.role = role;
        this.profiles = profiles;
        this.orders = orders;
        this.payments = payments;
        this.ratingEyeGlasses = ratingEyeGlasses;
        this.ratingLens = ratingLens;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Object getRole() {
        return role;
    }

    public void setRole(Object role) {
        this.role = role;
    }

    public List<Object> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Object> profiles) {
        this.profiles = profiles;
    }

    public List<Object> getOrders() {
        return orders;
    }

    public void setOrders(List<Object> orders) {
        this.orders = orders;
    }

    public List<Object> getPayments() {
        return payments;
    }

    public void setPayments(List<Object> payments) {
        this.payments = payments;
    }

    public List<Object> getRatingEyeGlasses() {
        return ratingEyeGlasses;
    }

    public void setRatingEyeGlasses(List<Object> ratingEyeGlasses) {
        this.ratingEyeGlasses = ratingEyeGlasses;
    }

    public List<Object> getRatingLens() {
        return ratingLens;
    }

    public void setRatingLens(List<Object> ratingLens) {
        this.ratingLens = ratingLens;
    }
}
