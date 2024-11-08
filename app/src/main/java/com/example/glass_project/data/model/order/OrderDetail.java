package com.example.glass_project.data.model.order;

public class OrderDetail {
    private int id;
    private int orderID;
    private int quantity;
    private boolean status;
    private ProductGlass productGlass;

    public OrderDetail(int id, int orderID, int quantity, boolean status, ProductGlass productGlass) {
        this.id = id;
        this.orderID = orderID;
        this.quantity = quantity;
        this.status = status;
        this.productGlass = productGlass;
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

    public ProductGlass getProductGlass() {
        return productGlass;
    }

    public void setProductGlass(ProductGlass productGlass) {
        this.productGlass = productGlass;
    }
}
