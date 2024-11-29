package com.example.glass_project.data.model.other;

public class Notification {
    private String id;
    private String title;
    private String body;

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    private String targetActivity;
    private String extraData;
    private String timestamp;
    private boolean isRead;

    // Constructor
    public Notification(String id, String title, String body, String targetActivity, String extraData, String timestamp, boolean isRead) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.targetActivity = targetActivity;
        this.extraData = extraData;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(String targetActivity) {
        this.targetActivity = targetActivity;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}


