package com.example.glass_project.data.model;

import java.util.List;

public class ResponseLensList {
    private List<Lens> data;
    private int totalCount;

    // Getters and Setters
    public List<Lens> getData() {
        return data;
    }

    public void setData(List<Lens> data) {
        this.data = data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
