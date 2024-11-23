package com.example.glass_project.data.model.rating;

public class RatingEyeGlass {
    private int eyeGlassID;
    private int accountID;
    private String name;
    private String imageUrl;
    private int score;
    private boolean status;

    public RatingEyeGlass(int eyeGlassID, int accountID, String name, String imageUrl, int score, boolean status) {
        this.eyeGlassID = eyeGlassID;
        this.accountID = accountID;
        this.name = name;
        this.imageUrl = imageUrl;
        this.score = score;
        this.status = status;
    }

    public int getEyeGlassID() {
        return eyeGlassID;
    }

    public void setEyeGlassID(int eyeGlassID) {
        this.eyeGlassID = eyeGlassID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getRating() {
        return score;
    }

    public void setRating(int rating) {
        this.score = rating;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
