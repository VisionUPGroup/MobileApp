package com.example.glass_project.data.model.notification;

public class DeviceTokenRequest {
    private String accountId;
    private String deviceToken;
    private String deviceInfo;

    public DeviceTokenRequest(String accountId, String deviceToken, String deviceInfo) {
        this.accountId = accountId;
        this.deviceToken = deviceToken;
        this.deviceInfo = deviceInfo;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }
}

