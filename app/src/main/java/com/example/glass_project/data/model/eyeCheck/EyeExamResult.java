package com.example.glass_project.data.model.eyeCheck;

import java.util.ArrayList;
import java.util.List;

public class EyeExamResult {
    private String examDate;
    private int visualAcuityRecordID;
    private double diopter;
    private int examID;
    private String eyeSide;
    public List<ExamResultItem> examResultItems;

    public EyeExamResult(String examDate, int visualAcuityRecordID, double diopter, int examID, String eyeSide) {
        this.examDate = examDate;
        this.visualAcuityRecordID = visualAcuityRecordID;
        this.diopter = diopter;
        this.examID = examID;
        this.eyeSide = eyeSide;
        this.examResultItems = new ArrayList<>();
    }


    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public int getVisualAcuityRecordID() {
        return visualAcuityRecordID;
    }

    public void setVisualAcuityRecordID(int visualAcuityRecordID) {
        this.visualAcuityRecordID = visualAcuityRecordID;
    }

    public double getDiopter() {
        return diopter;
    }

    public void setDiopter(double diopter) {
        this.diopter = diopter;
    }

    public int getExamID() {
        return examID;
    }

    public void setExamID(int examID) {
        this.examID = examID;
    }

    public String getEyeSide() {
        return eyeSide;
    }

    public void setEyeSide(String eyeSide) {
        this.eyeSide = eyeSide;
    }

    public List<ExamResultItem> getExamResultItems() {
        return examResultItems;
    }

    public void setExamResultItems(List<ExamResultItem> examResultItems) {
        this.examResultItems = examResultItems;
    }
    public static class ExamResultItem {
        private int examResultID;
        private int examItemID;
        private String actualResult;

        public ExamResultItem(int examResultID, int examItemID, String actualResult) {
            this.examResultID = examResultID;
            this.examItemID = examItemID;
            this.actualResult = actualResult;
        }

        public int getExamResultID() {
            return examResultID;
        }

        public void setExamResultID(int examResultID) {
            this.examResultID = examResultID;
        }

        public int getExamItemID() {
            return examItemID;
        }

        public void setExamItemID(int examItemID) {
            this.examItemID = examItemID;
        }

        public String getActualResult() {
            return actualResult;
        }

        public void setActualResult(String actualResult) {
            this.actualResult = actualResult;
        }
    }
}
