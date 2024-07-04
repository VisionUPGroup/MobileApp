package com.example.glass_project.data.model;

public class EyeGlassImage {
    private int id;
    private int eyeGlassID;
    private int angleView;
    private String url;

    public EyeGlassImage(int id, int eyeGlassID, int angleView, String url) {
        this.id = id;
        this.eyeGlassID = eyeGlassID;
        this.angleView = angleView;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEyeGlassID() {
        return eyeGlassID;
    }

    public void setEyeGlassID(int eyeGlassID) {
        this.eyeGlassID = eyeGlassID;
    }

    public int getAngleView() {
        return angleView;
    }

    public void setAngleView(int angleView) {
        this.angleView = angleView;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
