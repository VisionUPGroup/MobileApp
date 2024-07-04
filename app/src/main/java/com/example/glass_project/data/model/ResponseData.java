package com.example.glass_project.data.model;

import java.util.List;

public class ResponseData {
    private List<EyeGlass> data;

    public ResponseData(List<EyeGlass> data) {
        this.data = data;
    }

    public List<EyeGlass> getData() {
        return data;
    }

    public void setData(List<EyeGlass> data) {
        this.data = data;
    }

}
