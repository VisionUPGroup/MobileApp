package com.example.glass_project.data.model.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderPaymentDetail {
    @SerializedName("orderID")
    private int orderID;

    @SerializedName("productGlass")
    private List<ProductGlassPayment> productGlassPayments;

    @SerializedName("process")
    private int process;

    @SerializedName("isDeposit")
    private boolean isDeposit;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("totalPaid")
    private double totalPaid;

    @SerializedName("remainingAmount")
    private double remainingAmount;

    @SerializedName("payments")
    private List<Payment> payments;

    public OrderPaymentDetail(int orderID, List<ProductGlassPayment> productGlassPayments, int process, boolean isDeposit, double totalAmount, double totalPaid, double remainingAmount, List<Payment> payments) {
        this.orderID = orderID;
        this.productGlassPayments = productGlassPayments;
        this.process = process;
        this.isDeposit = isDeposit;
        this.totalAmount = totalAmount;
        this.totalPaid = totalPaid;
        this.remainingAmount = remainingAmount;
        this.payments = payments;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public List<ProductGlassPayment> getProductGlassPayments() {
        return productGlassPayments;
    }

    public void setProductGlassPayments(List<ProductGlassPayment> productGlassPayments) {
        this.productGlassPayments = productGlassPayments;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public boolean isDeposit() {
        return isDeposit;
    }

    public void setDeposit(boolean deposit) {
        isDeposit = deposit;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}

