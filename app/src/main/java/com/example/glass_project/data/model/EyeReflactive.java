package com.example.glass_project.data.model;

public class EyeReflactive {
    private int id;
    private String reflactiveName;
    private boolean status;

    // Các getters và setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReflactiveName() {
        return reflactiveName;
    }

    public void setReflactiveName(String reflactiveName) {
        this.reflactiveName = reflactiveName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
