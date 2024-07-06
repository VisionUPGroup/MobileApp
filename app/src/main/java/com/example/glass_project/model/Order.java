package com.example.glass_project.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order implements Serializable {
    private int id;
    private int accountId;
    private LocalDateTime orderDate;
    private boolean status;
    private Integer voucherId;
    private String senderAddress;
    private String receiverAddress;
    private String code;
    private BigDecimal total;
    private int process;

    public Order(int id, int accountId, LocalDateTime orderDate, boolean status, Integer voucherId, String senderAddress, String receiverAddress, String code, BigDecimal total, int process) {
        this.id = id;
        this.accountId = accountId;
        this.orderDate = orderDate;
        this.status = status;
        this.voucherId = voucherId;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.code = code;
        this.total = total;
        this.process = process;
    }

    public Order() {
    }

    public Order(int id, boolean status, String senderAddress, String receiverAddress, String code, int process) {
        this.id = id;
        this.status = status;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.code = code;
        this.process = process;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }
}
