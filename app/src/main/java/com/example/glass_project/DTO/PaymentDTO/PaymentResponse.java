package com.example.glass_project.DTO.PaymentDTO;

import java.io.Serializable;
import java.util.Date;


public class PaymentResponse implements Serializable {
    private String paymentUrl;
    private int id;
    private int accountID;
    private int orderID;
    private String paymentMethod;
    private Date date;
    private double totalAmount;
    private boolean status;

    // Constructors, getters, and setters
    // Constructor with parameters
    public PaymentResponse(String paymentUrl, int id, int accountID, int orderID, String paymentMethod,
                          Date date, double totalAmount, boolean status) {
        this.paymentUrl = paymentUrl;
        this.id = id;
        this.accountID = accountID;
        this.orderID = orderID;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
