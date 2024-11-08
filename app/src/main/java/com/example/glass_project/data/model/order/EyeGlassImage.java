package com.example.glass_project.data.model.order;

public class EyeGlassImage {
    private int id;
    private String url;

    public EyeGlassImage(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
