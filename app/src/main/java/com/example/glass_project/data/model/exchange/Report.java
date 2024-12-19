package com.example.glass_project.data.model.exchange;

public class Report {
    private int id;
    private int type;
    private int status;
    private String description;

    public Report(int id, int type, int status, String description) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
