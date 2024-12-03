package com.example.glass_project.data.model.report;

import java.util.List;

public class ReportResponse {
    private List<ReportData> data;
    private int totalCount;

    public ReportResponse(List<ReportData> data, int totalCount) {
        this.data = data;
        this.totalCount = totalCount;
    }

    // Getters and Setters
    public List<ReportData> getData() {
        return data;
    }

    public void setData(List<ReportData> data) {
        this.data = data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
