package com.example.glass_project.data.model.order;

import com.google.gson.annotations.SerializedName;

public class Lens {
    private int id;

    @SerializedName("lensName")
    private String lensName;

    private String lensDescription;

    @SerializedName(value = "leftLensImage", alternate = "rightLensImage")
    private String lensImage;

    public Lens(int id, String lensName, String lensDescription, String lensImage) {
        this.id = id;
        this.lensName = lensName;
        this.lensDescription = lensDescription;
        this.lensImage = lensImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLensName() {
        return lensName;
    }

    public void setLensName(String lensName) {
        this.lensName = lensName;
    }

    public String getLensDescription() {
        return lensDescription;
    }

    public void setLensDescription(String lensDescription) {
        this.lensDescription = lensDescription;
    }

    public String getLensImage() {
        return lensImage;
    }

    public void setLensImage(String lensImage) {
        this.lensImage = lensImage;
    }
}
