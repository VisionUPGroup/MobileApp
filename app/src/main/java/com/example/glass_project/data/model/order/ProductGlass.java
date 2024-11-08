package com.example.glass_project.data.model.order;

public class ProductGlass {
    private int id;
    private EyeGlass eyeGlass;
    private double total;
    private ProductGlasses productGlassDetail;

    public ProductGlass(int id, EyeGlass eyeGlass, double total) {
        this.id = id;
        this.eyeGlass = eyeGlass;
        this.total = total;
    }
    public ProductGlasses getProductGlassDetail() {
        return productGlassDetail;
    }

    public void setProductGlassDetail(ProductGlasses productGlassDetail) {
        this.productGlassDetail = productGlassDetail;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EyeGlass getEyeGlass() {
        return eyeGlass;
    }

    public void setEyeGlass(EyeGlass eyeGlass) {
        this.eyeGlass = eyeGlass;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
