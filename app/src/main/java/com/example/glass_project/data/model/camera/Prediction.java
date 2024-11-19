package com.example.glass_project.data.model.camera;

public class Prediction {
    private String tagName;
    private double probability;

    public Prediction(String tagName, double probability) {
        this.tagName = tagName;
        this.probability = probability;
    }

    public String getTagName() {
        return tagName;
    }

    public double getProbability() {
        return probability;
    }
}
