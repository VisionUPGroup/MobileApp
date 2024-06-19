package com.example.glass_project.data.model;

public class Glass {
    private int glassId;
    private String imageGlass;
    private String nameGlass;
    private double price;

    public Glass(int glassId, String imageGlass, String nameGlass, double price) {
        this.glassId = glassId;
        this.imageGlass = imageGlass;
        this.nameGlass = nameGlass;
        this.price = price;
    }

    public int getGlassId() {
        return glassId;
    }

    public void setGlassId(int glassId) {
        this.glassId = glassId;
    }

    public String getImageGlass() {
        return imageGlass;
    }

    public void setImageGlass(String imageGlass) {
        this.imageGlass = imageGlass;
    }

    public String getNameGlass() {
        return nameGlass;
    }

    public void setNameGlass(String nameGlass) {
        this.nameGlass = nameGlass;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
