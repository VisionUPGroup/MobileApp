package com.example.glass_project.model;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private int id;

    private int orderId;

    private int productGlassID;

    private int quantity;

    private boolean status;

    public OrderDetail(int id, int orderId, int productGlassId, int quantity, boolean status) {
        this.id = id;
        this.orderId = orderId;
        this.productGlassID = productGlassId;
        this.quantity = quantity;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductGlassId() {
        return productGlassID;
    }

    public void setProductGlassId(int productGlassId) {
        this.productGlassID = productGlassId;
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
}
