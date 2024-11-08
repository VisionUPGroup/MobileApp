package com.example.glass_project.data.model.order;

import java.util.List;

public class OrderHistoryResponse {
    private List<OrderHistoryItem> data;
    private int totalCount;
    private double totalRevenue;

    public OrderHistoryResponse(List<OrderHistoryItem> data, int totalCount, double totalRevenue) {
        this.data = data;
        this.totalCount = totalCount;
        this.totalRevenue = totalRevenue;
    }

    public List<OrderHistoryItem> getData() {
        return data;
    }

    public void setData(List<OrderHistoryItem> data) {
        this.data = data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
