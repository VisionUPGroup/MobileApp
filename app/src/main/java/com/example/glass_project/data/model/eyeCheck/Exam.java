package com.example.glass_project.data.model.eyeCheck;

public class Exam {
    private int id;
    private String type;
    private int imageResource;
    private boolean status;

    public Exam(int id, String type,int imageResource, boolean status) {
        this.id = id;
        this.type = type;
        this.imageResource = imageResource;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
    public void setType(String type) {
        this.type = type;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return type; // hoặc thuộc tính bạn muốn hiển thị
    }
}
