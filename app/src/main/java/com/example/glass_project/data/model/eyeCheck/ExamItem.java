package com.example.glass_project.data.model.eyeCheck;

public class ExamItem {
    private int id;
    private int rotation;
    private String type;
    private int level;
    private int standardDistance;
    private String url;
    private int content;
    private double myopia;
    private String expectedAnswer;
    private int examID;

    public ExamItem(int id, int rotation, String type, int level, int standardDistance, String url, int content, double myopia, String expectedAnswer, int examID) {
        this.id = id;
        this.rotation = rotation;
        this.type = type;
        this.level = level;
        this.standardDistance = standardDistance;
        this.url = url;
        this.content = content;
        this.myopia = myopia;
        this.expectedAnswer = expectedAnswer;
        this.examID = examID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStandardDistance() {
        return standardDistance;
    }

    public void setStandardDistance(int standardDistance) {
        this.standardDistance = standardDistance;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }

    public double getMyopia() {
        return myopia;
    }

    public void setMyopia(double myopia) {
        this.myopia = myopia;
    }

    public String getExpectedAnswer() {
        return expectedAnswer;
    }

    public void setExpectedAnswer(String expectedAnswer) {
        this.expectedAnswer = expectedAnswer;
    }

    public int getExamID() {
        return examID;
    }

    public void setExamID(int examID) {
        this.examID = examID;
    }
}
