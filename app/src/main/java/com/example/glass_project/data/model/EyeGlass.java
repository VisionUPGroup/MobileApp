package com.example.glass_project.data.model;

import java.util.List;

public class EyeGlass {
    private int id;
    private int eyeGlassTypeID;
    private String name;
    private double price;
    private int rate;
    private int rateCount;
    private int quantity;
    private String material;
    private String color;
    private int lensWidth;
    private int lensHeight;
    private int bridgeWidth;
    private int templeLength;
    private int frameWidth;
    private String style;
    private int weight;
    private String design;
    private boolean status;
    private EyeGlassType eyeGlassType;
    private List<EyeGlassImage> eyeGlassImages;

    public EyeGlass(int id, int eyeGlassTypeID, String name, double price, int rate, int rateCount, int quantity, String material, String color, int lensWidth, int lensHeight, int bridgeWidth, int templeLength, int frameWidth, String style, int weight, String design, boolean status, EyeGlassType eyeGlassType, List<EyeGlassImage> eyeGlassImages) {
        this.id = id;
        this.eyeGlassTypeID = eyeGlassTypeID;
        this.name = name;
        this.price = price;
        this.rate = rate;
        this.rateCount = rateCount;
        this.quantity = quantity;
        this.material = material;
        this.color = color;
        this.lensWidth = lensWidth;
        this.lensHeight = lensHeight;
        this.bridgeWidth = bridgeWidth;
        this.templeLength = templeLength;
        this.frameWidth = frameWidth;
        this.style = style;
        this.weight = weight;
        this.design = design;
        this.status = status;
        this.eyeGlassType = eyeGlassType;
        this.eyeGlassImages = eyeGlassImages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEyeGlassTypeID() {
        return eyeGlassTypeID;
    }

    public void setEyeGlassTypeID(int eyeGlassTypeID) {
        this.eyeGlassTypeID = eyeGlassTypeID;
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

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getRateCount() {
        return rateCount;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getLensWidth() {
        return lensWidth;
    }

    public void setLensWidth(int lensWidth) {
        this.lensWidth = lensWidth;
    }

    public int getLensHeight() {
        return lensHeight;
    }

    public void setLensHeight(int lensHeight) {
        this.lensHeight = lensHeight;
    }

    public int getBridgeWidth() {
        return bridgeWidth;
    }

    public void setBridgeWidth(int bridgeWidth) {
        this.bridgeWidth = bridgeWidth;
    }

    public int getTempleLength() {
        return templeLength;
    }

    public void setTempleLength(int templeLength) {
        this.templeLength = templeLength;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public EyeGlassType getEyeGlassType() {
        return eyeGlassType;
    }

    public void setEyeGlassType(EyeGlassType eyeGlassType) {
        this.eyeGlassType = eyeGlassType;
    }

    public List<EyeGlassImage> getEyeGlassImages() {
        return eyeGlassImages;
    }

    public void setEyeGlassImages(List<EyeGlassImage> eyeGlassImages) {
        this.eyeGlassImages = eyeGlassImages;
    }
}
