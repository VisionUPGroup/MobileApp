package com.example.glass_project.DTO.PaymentDTO;

import java.io.Serializable;

public class PaymentGetLinkRequest implements Serializable {
    private double amount;
    private int accountID;
    private int orderID;

    public PaymentGetLinkRequest(double amount, int accountID, int orderID) {
        this.amount = amount;
        this.accountID = accountID;
        this.orderID = orderID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
}
