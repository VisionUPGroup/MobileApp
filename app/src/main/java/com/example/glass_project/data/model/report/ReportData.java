package com.example.glass_project.data.model.report;


public class ReportData {
    private int id;
    private int orderID;
    private Handler handler;  // Sửa thành kiểu Handler
    private String description;
    private String feedback;
    private int status;
    private int type;

    public ReportData(int id, int orderID, Handler handler, String description, String feedback, int status, int type) {
        this.id = id;
        this.orderID = orderID;
        this.handler = handler;
        this.description = description;
        this.feedback = feedback;
        this.status = status;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

