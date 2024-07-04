package com.example.glass_project.data.model;

public class EyeGlassType {
    private int id;
    private String glassType;
    private boolean status;


    public EyeGlassType(int id, String glassType, boolean status) {
        this.id = id;
        this.glassType = glassType;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGlassType() {
        return glassType;
    }

    public void setGlassType(String glassType) {
        this.glassType = glassType;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
