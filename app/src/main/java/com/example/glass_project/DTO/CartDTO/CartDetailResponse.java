package com.example.glass_project.DTO.CartDTO;

import com.example.glass_project.data.model.EyeGlassImage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public class CartDetailResponse implements Serializable {
    private int accountID;
    private int productGlassID;
    private int quantity;
    private String eyeGlassName;
    private BigDecimal eyeGlassPrice;
    private String lensName;
    private BigDecimal lensPrice;
    private BigDecimal totalPriceProductGlass;
    private List<EyeGlassImage> eyeGlassImages;

    // Getters and Setters
    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getProductGlassID() {
        return productGlassID;
    }

    public void setProductGlassID(int productGlassID) {
        this.productGlassID = productGlassID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getEyeGlassName() {
        return eyeGlassName;
    }

    public void setEyeGlassName(String eyeGlassName) {
        this.eyeGlassName = eyeGlassName;
    }

    public BigDecimal getEyeGlassPrice() {
        return eyeGlassPrice;
    }

    public void setEyeGlassPrice(BigDecimal eyeGlassPrice) {
        this.eyeGlassPrice = eyeGlassPrice;
    }

    public String getLensName() {
        return lensName;
    }

    public void setLensName(String lensName) {
        this.lensName = lensName;
    }

    public BigDecimal getLensPrice() {
        return lensPrice;
    }

    public void setLensPrice(BigDecimal lensPrice) {
        this.lensPrice = lensPrice;
    }

    public BigDecimal getTotalPriceProductGlass() {
        return totalPriceProductGlass;
    }

    public void setTotalPriceProductGlass(BigDecimal totalPriceProductGlass) {
        this.totalPriceProductGlass = totalPriceProductGlass;
    }

    public List<EyeGlassImage> getEyeGlassImages() {
        return eyeGlassImages;
    }

    public void setEyeGlassImages(List<EyeGlassImage> eyeGlassImages) {
        this.eyeGlassImages = eyeGlassImages;
    }
}
