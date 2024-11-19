package com.example.glass_project.data.model.profile;

import java.io.Serializable;

public class Profile implements Serializable {
    private int id;
    private int accountID;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String urlImage;
    private String birthday;
    private boolean status;
    private boolean expanded = false;

    public Profile(int id, int accountID, String fullName, String phoneNumber, String address, String urlImage, String birthday, boolean status) {
        this.id = id;
        this.accountID = accountID;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.urlImage = urlImage;
        this.birthday = birthday;
        this.status = status;
    }
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id +" - "+ fullName+" - "+phoneNumber; // hoặc thuộc tính bạn muốn hiển thị
    }
}
