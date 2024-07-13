package com.example.glass_project.DTO.CartDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CartSummaryResponse implements Serializable {
    private List<CartDetailResponse> cartDetails;
    private int totalItems;
    private BigDecimal totalPrice;

    // Getters and Setters
    public List<CartDetailResponse> getCartDetails() {
        return cartDetails;
    }

    public void setCartDetails(List<CartDetailResponse> cartDetails) {
        this.cartDetails = cartDetails;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
