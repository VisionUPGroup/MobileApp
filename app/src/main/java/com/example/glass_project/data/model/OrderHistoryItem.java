package com.example.glass_project.data.model;

import com.example.glass_project.DTO.OrderDetailDTO.OrderDetailDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderHistoryItem {
    private int id;
    private int accountID;
    private String orderDate;
    private boolean status;
    private String senderAddress;
    private String receiverAddress;
    private String code;
    private double total;
    private int process; // Process status enum or integer
    private List<OrderDetailDTO> orderDetails; // List of order details

    public OrderHistoryItem(int id, int accountID, String orderDate, boolean status, String senderAddress, String receiverAddress, String code, double total, int process, List<OrderDetailDTO> orderDetails) {
        this.id = id;
        this.accountID = accountID;
        this.orderDate = orderDate;
        this.status = status;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.code = code;
        this.total = total;
        this.process = process;
        this.orderDetails = orderDetails;
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public List<OrderDetailDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }

    // Format orderDate to "dd/MM/yyyy HH:mm" and sort by descending orderDate
    public static List<OrderHistoryItem> formatAndSort(List<OrderHistoryItem> orderHistoryItems) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Format orderDate and sort
        List<OrderHistoryItem> sortedItems = new ArrayList<>(orderHistoryItems);
        sortedItems.sort((item1, item2) -> {
            try {
                Date date1 = inputFormat.parse(item1.getOrderDate());
                Date date2 = inputFormat.parse(item2.getOrderDate());
                return date2.compareTo(date1); // Descending order
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        // Update orderDate to formatted string
        for (OrderHistoryItem item : sortedItems) {
            try {
                Date date = inputFormat.parse(item.getOrderDate());
                item.setOrderDate(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return sortedItems;
    }
}
