package com.example.glass_project.data.model.order;

import java.util.List;

public class EyeGlass {
    private int id;
    private String name;
    private double price;
    private List<EyeGlassImage> eyeGlassImages;

    public EyeGlass(int id, String name, double price, List<EyeGlassImage> eyeGlassImages) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.eyeGlassImages = eyeGlassImages;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<EyeGlassImage> getEyeGlassImages() {
        return eyeGlassImages;
    }

    public void setEyeGlassImages(List<EyeGlassImage> eyeGlassImages) {
        this.eyeGlassImages = eyeGlassImages;
    }
}
