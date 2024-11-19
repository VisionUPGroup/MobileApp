package com.example.glass_project.data.model.rating;

public class RatingEyeGlass {
    private int eyeGlassID;
    private String eyeGlassName;
    private String imageUrl;
    private float rating;

    public RatingEyeGlass(int eyeGlassID, String eyeGlassName, String imageUrl, float rating) {
        this.eyeGlassID = eyeGlassID;
        this.eyeGlassName = eyeGlassName;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    public int getEyeGlassID() {
        return eyeGlassID;
    }

    public void setEyeGlassID(int eyeGlassID) {
        this.eyeGlassID = eyeGlassID;
    }

    public String getEyeGlassName() {
        return eyeGlassName;
    }

    public void setEyeGlassName(String eyeGlassName) {
        this.eyeGlassName = eyeGlassName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
