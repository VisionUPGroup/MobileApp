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
}
