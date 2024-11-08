package com.example.glass_project.data.model.order;

public class Payment {
    private int id;
    private double totalAmount;
    private String date;
    private String paymentMethod;

    public Payment(int id, double totalAmount, String date, String paymentMethod) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.date = date;
        this.paymentMethod = paymentMethod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
