package com.example.glass_project.data.model;

import java.util.List;

public class LensType {
    private int id;
    private String description;
    private boolean status;
    private List<Lens> lens;

    public LensType(int id, String description, boolean status) {
        this.id = id;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Lens> getLens() {
        return lens;
    }

    public void setLens(List<Lens> lens) {
        this.lens = lens;
    }

}
