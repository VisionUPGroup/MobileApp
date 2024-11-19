package com.example.glass_project.data.model.camera;

import java.util.List;

public class PredictionResponse {
    private List<Prediction> predictions;

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public static class Prediction {
        private String tagName;
        private double probability;
        private BoundingBox boundingBox;

        public String getTagName() {
            return tagName;
        }

        public double getProbability() {
            return probability;
        }

        public BoundingBox getBoundingBox() {
            return boundingBox;
        }

        public static class BoundingBox {
            private double left;
            private double top;
            private double width;
            private double height;

            public double getLeft() {
                return left;
            }

            public double getTop() {
                return top;
            }

            public double getWidth() {
                return width;
            }

            public double getHeight() {
                return height;
            }
        }
    }
}
