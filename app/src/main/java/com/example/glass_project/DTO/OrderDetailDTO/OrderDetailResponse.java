package com.example.glass_project.DTO.OrderDetailDTO;

import java.io.Serializable;

public class OrderDetailResponse implements Serializable {
    private int id;
    private int orderID;
    private int productGlassID;
    private int quantity;
    private boolean status;

    public OrderDetailResponse(int id, int orderID, int productGlassID, int quantity, boolean status) {
        this.id = id;
        this.orderID = orderID;
        this.productGlassID = productGlassID;
        this.quantity = quantity;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
