package com.example.glass_project.data.model.order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderHistoryItem {
    private int id;
    private int accountID;
    private String orderTime;
    private boolean status;
    private String receiverAddress;
    private int total;
    private Integer voucherID;  // Nullable integer for voucherID
    private String code;
    private int process;
    private Kiosk kiosks;  // This can be null
    private boolean isDeposit;
    private String shippingStartTime;
    private String deliveriedStartTime;
    private Kiosk placedByKiosk;  // This can also be null
    private String deliveryConfirmationImage;
    private Shipper shipper;
    private List<OrderDetail> orderDetails;  // List of order details

    public OrderHistoryItem(int id, int accountID, String orderTime, boolean status, String receiverAddress, int total, Integer voucherID, String code, int process, Kiosk kiosks, boolean isDeposit, String shippingStartTime, String deliveriedStartTime, Kiosk placedByKiosk, String deliveryConfirmationImage, Shipper shipper, List<OrderDetail> orderDetails) {
        this.id = id;
        this.accountID = accountID;
        this.orderTime = orderTime;
        this.status = status;
        this.receiverAddress = receiverAddress;
        this.total = total;
        this.voucherID = voucherID;
        this.code = code;
        this.process = process;
        this.kiosks = kiosks;
        this.isDeposit = isDeposit;
        this.shippingStartTime = shippingStartTime;
        this.deliveriedStartTime = deliveriedStartTime;
        this.placedByKiosk = placedByKiosk;
        this.deliveryConfirmationImage = deliveryConfirmationImage;
        this.shipper = shipper;
        this.orderDetails = orderDetails;
    }

    public String getFormattedOrderTime() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = inputFormat.parse(orderTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return orderTime;
        }
    }

    public String getProcessStatus() {
        switch (process) {
            case 0: return "Pending";
            case 1: return "Processing";
            case 2: return "Shipping";
            case 3: return "Delivered";
            case 4: return "Completed";
            case 5: return "Cancelled";
            default: return "Unknown";
        }
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

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Integer getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(Integer voucherID) {
        this.voucherID = voucherID;
    }

    public String getShippingStartTime() {
        return shippingStartTime;
    }

    public void setShippingStartTime(String shippingStartTime) {
        this.shippingStartTime = shippingStartTime;
    }

    public String getDeliveriedStartTime() {
        return deliveriedStartTime;
    }

    public void setDeliveriedStartTime(String deliveriedStartTime) {
        this.deliveriedStartTime = deliveriedStartTime;
    }

    public Kiosk getPlacedByKiosk() {
        return placedByKiosk;
    }

    public void setPlacedByKiosk(Kiosk placedByKiosk) {
        this.placedByKiosk = placedByKiosk;
    }

    public String getDeliveryConfirmationImage() {
        return deliveryConfirmationImage;
    }

    public void setDeliveryConfirmationImage(String deliveryConfirmationImage) {
        this.deliveryConfirmationImage = deliveryConfirmationImage;
    }

    public Shipper getShipper() {
        return shipper;
    }

    public void setShipper(Shipper shipper) {
        this.shipper = shipper;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public List<OrderDetail> getOrderDetails() { // Updated method
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) { // Updated method
        this.orderDetails = orderDetails;
    }

    public Kiosk getKiosks() {
        return kiosks;  // Return Kiosk object
    }

    public void setKiosks(Kiosk kiosks) {
        this.kiosks = kiosks;  // Set Kiosk object
    }
}
