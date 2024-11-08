package com.example.glass_project.data.model.order;

import com.google.gson.annotations.SerializedName;

public class EyeGlassPayment {
    private int id;
    private String name;

    @SerializedName("eyeGlassImage")
    private String eyeGlassImage;

    public EyeGlassPayment(int id, String name, String eyeGlassImage) {
        this.id = id;
        this.name = name;
        this.eyeGlassImage = eyeGlassImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEyeGlassImage() {
        return eyeGlassImage;
    }

    public void setEyeGlassImage(String eyeGlassImage) {
        this.eyeGlassImage = eyeGlassImage;
    }
}
