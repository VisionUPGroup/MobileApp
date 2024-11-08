package com.example.glass_project.data.model;

public class Kiosk {
    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private int accountID;
    private String openingHours;
    private String createdAt;
    private String updatedAt;
    private boolean status;

    // Constructor
    public Kiosk(int id, String name, String address, String phoneNumber,
                 String email, int accountID, String openingHours,
                 String createdAt, String updatedAt, boolean status) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.accountID = accountID;
        this.openingHours = openingHours;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public int getAccountID() { return accountID; }
    public String getOpeningHours() { return openingHours; }
    public boolean isStatus() { return status; }
}

