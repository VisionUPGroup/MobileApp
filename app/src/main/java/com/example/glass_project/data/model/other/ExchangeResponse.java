package com.example.glass_project.data.model.other;

import java.util.List;

public class ExchangeResponse {
    private List<ExchangeItem> data;
    private int totalCount;

    // Getters và Setters
    public List<ExchangeItem> getData() {
        return data;
    }

    public void setData(List<ExchangeItem> data) {
        this.data = data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
