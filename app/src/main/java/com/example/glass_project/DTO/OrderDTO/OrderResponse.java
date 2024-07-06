package com.example.glass_project.DTO.OrderDTO;

import com.example.glass_project.DTO.OrderDetailDTO.OrderDetailResponse;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse implements Serializable {
    private int id;
    private int accountID;
    private String orderDate;
    private boolean status;
    private Integer voucherID;
    private String senderAddress;
    private String receiverAddress;
    private String code;
    private BigDecimal total;
    private int process;
    private List<OrderDetailResponse> orderDetails;

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public Integer getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(Integer voucherID) {
        this.voucherID = voucherID;
    }

    public List<OrderDetailResponse> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailResponse> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public OrderResponse(int id, int accountID, String orderDate, boolean status, Integer voucherID, String senderAddress, String receiverAddress, String code, BigDecimal total, int process, List<OrderDetailResponse> orderDetails) {
        this.id = id;
        this.accountID = accountID;
        this.orderDate = orderDate;
        this.status = status;
        this.voucherID = voucherID;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.code = code;
        this.total = total;
        this.process = process;
        this.orderDetails = orderDetails;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountID;
    }

    public void setAccountId(int accountId) {
        this.accountID = accountId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getVoucherId() {
        return voucherID;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherID = voucherId;
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
