package com.example.glass_project.data.model.report;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportData {
    private int id;
    private int orderID;
    private Handler handler;
    private String description;
    private String evidenceImage;
    private int productGlassID;
    private String feedback;
    private int status;
    private int type;

    private String createAt;
    private String updateAt;

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public ReportData(int id, int orderID, Handler handler, String description, String feedback, int status, int type) {
        this.id = id;
        this.orderID = orderID;
        this.handler = handler;
        this.description = description;
        this.feedback = feedback;
        this.status = status;
        this.type = type;
    }

    public String getEvidenceImage() {
        return evidenceImage;
    }

    public void setEvidenceImage(String evidenceImage) {
        this.evidenceImage = evidenceImage;
    }

    public int getProductGlassID() {
        return productGlassID;
    }

    public void setProductGlassID(int productGlassID) {
        this.productGlassID = productGlassID;
    }

    public String getFormattedCreateAt() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = inputFormat.parse(createAt);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return createAt;
        }
    }
    public String getFormattedUpdateAt() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = inputFormat.parse(updateAt);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return updateAt;
        }
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

