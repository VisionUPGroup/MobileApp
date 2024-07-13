package com.example.glass_project.data.model;

import java.util.List;

public class Lens {
    private int id;
    private String lensName;
    private String lensDescription;
    private double lensPrice;
    private int quantity;
    private boolean status;
    private int rate;
    private int rateCount;
    private int lensTypeID;
    private int eyeReflactiveID;
    private EyeReflactive eyeReflactive;
    private LensType lensType;
    private List<String> lensImages;
    private List<Object> ratingLens;

    // Các getters và setters
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

    public double getLensPrice() {
        return lensPrice;
    }

    public void setLensPrice(double lensPrice) {
        this.lensPrice = lensPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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

    public int getLensTypeID() {
        return lensTypeID;
    }

    public void setLensTypeID(int lensTypeID) {
        this.lensTypeID = lensTypeID;
    }

    public int getEyeReflactiveID() {
        return eyeReflactiveID;
    }

    public void setEyeReflactiveID(int eyeReflactiveID) {
        this.eyeReflactiveID = eyeReflactiveID;
    }

    public EyeReflactive getEyeReflactive() {
        return eyeReflactive;
    }

    public void setEyeReflactive(EyeReflactive eyeReflactive) {
        this.eyeReflactive = eyeReflactive;
    }

    public LensType getLensType() {
        return lensType;
    }

    public void setLensType(LensType lensType) {
        this.lensType = lensType;
    }

    public List<String> getLensImages() {
        return lensImages;
    }

    public void setLensImages(List<String> lensImages) {
        this.lensImages = lensImages;
    }

    public List<Object> getRatingLens() {
        return ratingLens;
    }

    public void setRatingLens(List<Object> ratingLens) {
        this.ratingLens = ratingLens;
    }
}

