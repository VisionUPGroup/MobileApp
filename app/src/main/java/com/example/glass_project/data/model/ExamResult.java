package com.example.glass_project.data.model;

public class ExamResult {
    private int id;
    private String examDate;
    private double diopter;
    private boolean status;
    private String eyeSide;

    public ExamResult(int id, String examDate, double diopter, boolean status, String eyeSide) {
        this.id = id;
        this.examDate = examDate;
        this.diopter = diopter;
        this.status = status;
        this.eyeSide = eyeSide;
    }

    public int getId() {
        return id;
    }

    public String getExamDate() {
        return examDate;
    }

    public double getDiopter() {
        return diopter;
    }

    public boolean isStatus() {
        return status;
    }

    public String getEyeSide() {
        return eyeSide;
    }
}
