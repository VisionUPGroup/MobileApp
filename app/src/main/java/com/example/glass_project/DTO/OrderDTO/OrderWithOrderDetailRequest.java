package com.example.glass_project.DTO.OrderDTO;

import com.example.glass_project.DTO.OrderDetailDTO.OrderDetailRequest;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public class OrderWithOrderDetailRequest implements Serializable {
    private int id;
    private int accountId;
    private String receiverAddress;
    private String orderDate;
    private double total;
    private transient int voucherId;
    private boolean status;
    private List<OrderDetailRequest> orderDetails;

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

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<OrderDetailRequest> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailRequest> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public OrderWithOrderDetailRequest(int id, int accountId, String receiverAddress, String orderDate,
                    double total, int voucherId, boolean status, List<OrderDetailRequest> orderDetails) {
        this.id = id;
        this.accountId = accountId;
        this.receiverAddress = receiverAddress;
        this.orderDate = orderDate;
        this.total = total;
        this.voucherId = voucherId;
        this.status = status;
        this.orderDetails = orderDetails;
    }



    public OrderWithOrderDetailRequest(int id, int accountId, String receiverAddress, String orderDate,
                                       double total, boolean status, List<OrderDetailRequest> orderDetails) {
        this.id = id;
        this.accountId = accountId;
        this.receiverAddress = receiverAddress;
        this.orderDate = orderDate;
        this.total = total;
        this.status = status;
        this.orderDetails = orderDetails;
    }

    public OrderWithOrderDetailRequest() {
    }


}
