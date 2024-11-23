package com.example.glass_project.data.model.rating;

public class RatingPayload {
    private int eyeGlassID;
    private int accountID;
    private int score;
    private boolean status;

    public RatingPayload(int eyeGlassID, int accountID, int score, boolean status) {
        this.eyeGlassID = eyeGlassID;
        this.accountID = accountID;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
// Getters và Setters nếu cần
}

