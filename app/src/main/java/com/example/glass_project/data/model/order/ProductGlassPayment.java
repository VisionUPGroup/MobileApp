package com.example.glass_project.data.model.order;

import com.google.gson.annotations.SerializedName;

public class ProductGlassPayment {
    @SerializedName("productGlassID")
    private int productGlassID;

    @SerializedName("eyeGlass")
    private EyeGlassPayment eyeGlass;

    @SerializedName("leftLen")
    private Lens leftLens;

    @SerializedName("rightLen")
    private Lens rightLens;

    public ProductGlassPayment(int productGlassID, EyeGlassPayment eyeGlass, Lens leftLens, Lens rightLens) {
        this.productGlassID = productGlassID;
        this.eyeGlass = eyeGlass;
        this.leftLens = leftLens;
        this.rightLens = rightLens;
    }

    public int getProductGlassID() {
        return productGlassID;
    }

    public void setProductGlassID(int productGlassID) {
        this.productGlassID = productGlassID;
    }

    public EyeGlassPayment getEyeGlass() {
        return eyeGlass;
    }

    public void setEyeGlass(EyeGlassPayment eyeGlass) {
        this.eyeGlass = eyeGlass;
    }

    public Lens getLeftLens() {
        return leftLens;
    }

    public void setLeftLens(Lens leftLens) {
        this.leftLens = leftLens;
    }

    public Lens getRightLens() {
        return rightLens;
    }

    public void setRightLens(Lens rightLens) {
        this.rightLens = rightLens;
    }
}
