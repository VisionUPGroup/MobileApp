package com.example.glass_project.DTO.OrderDetailDTO;
public class OrderDetailDTO {
    private long id;
    private long productGlassId;
    private int quantity;
    private String eyeGlassName;
    private double eyeGlassPrice;
    private String lensName;
    private double lensPrice;

    public OrderDetailDTO(long id, long productGlassId, int quantity, String eyeGlassName, double eyeGlassPrice, String lensName, double lensPrice) {
        this.id = id;
        this.productGlassId = productGlassId;
        this.quantity = quantity;
        this.eyeGlassName = eyeGlassName;
        this.eyeGlassPrice = eyeGlassPrice;
        this.lensName = lensName;
        this.lensPrice = lensPrice;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductGlassId() {
        return productGlassId;
    }

    public void setProductGlassId(long productGlassId) {
        this.productGlassId = productGlassId;
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

    public double getEyeGlassPrice() {
        return eyeGlassPrice;
    }

    public void setEyeGlassPrice(double eyeGlassPrice) {
        this.eyeGlassPrice = eyeGlassPrice;
    }

    public String getLensName() {
        return lensName;
    }

    public void setLensName(String lensName) {
        this.lensName = lensName;
    }

    public double getLensPrice() {
        return lensPrice;
    }

    public void setLensPrice(double lensPrice) {
        this.lensPrice = lensPrice;
    }
}

