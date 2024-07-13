package com.example.glass_project.data.model.response;

public class CartResponse {
    private int accountId;
    private int productGlassID ;
    private int quantity;

    public CartResponse(int accountId, int productGlassID, int quantity) {
        this.accountId = accountId;
        this.productGlassID = productGlassID;
        this.quantity = quantity;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
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
}
