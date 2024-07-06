package com.example.glass_project.DTO.OrderDetailDTO;

import java.io.Serializable;

public class OrderDetailRequest implements Serializable {
    private int id;
    private int orderId;
    private int productGlassId;
    private int quantity;
    private boolean status;

    public OrderDetailRequest(int id, int orderId, int productGlassId, int quantity, boolean status) {
        this.id = id;
        this.orderId = orderId;
        this.productGlassId = productGlassId;
        this.quantity = quantity;
        this.status = status;
    }

    public OrderDetailRequest() {
    }

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
        return productGlassId;
    }

    public void setProductGlassId(int productGlassId) {
        this.productGlassId = productGlassId;
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
