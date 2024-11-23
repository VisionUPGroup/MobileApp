package com.example.glass_project.data.model.order;

public class ProductGlasses {
    private int eyeGlassID;
    private int leftLenID;
    private int rightLenID;
    private int accountID;
    private int total;
    private float  sphereOD;
    private float  cylinderOD;
    private float  axisOD;
    private float  sphereOS;
    private float  cylinderOS;
    private float  axisOS;
    private float  addOD;
    private float  addOS;
    private int pd;

    public ProductGlasses(int eyeGlassID, int leftLenID, int rightLenID, int accountID, int total, float sphereOD, float cylinderOD, float axisOD, float sphereOS, float cylinderOS, float axisOS, float addOD, float addOS, int pd) {
        this.eyeGlassID = eyeGlassID;
        this.leftLenID = leftLenID;
        this.rightLenID = rightLenID;
        this.accountID = accountID;
        this.total = total;
        this.sphereOD = sphereOD;
        this.cylinderOD = cylinderOD;
        this.axisOD = axisOD;
        this.sphereOS = sphereOS;
        this.cylinderOS = cylinderOS;
        this.axisOS = axisOS;
        this.addOD = addOD;
        this.addOS = addOS;
        this.pd = pd;
    }

    public int getEyeGlassID() {
        return eyeGlassID;
    }

    public void setEyeGlassID(int eyeGlassID) {
        this.eyeGlassID = eyeGlassID;
    }

    public int getLeftLenID() {
        return leftLenID;
    }

    public void setLeftLenID(int leftLenID) {
        this.leftLenID = leftLenID;
    }

    public int getRightLenID() {
        return rightLenID;
    }

    public void setRightLenID(int rightLenID) {
        this.rightLenID = rightLenID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public float getSphereOD() {
        return sphereOD;
    }

    public void setSphereOD(float sphereOD) {
        this.sphereOD = sphereOD;
    }

    public float getCylinderOD() {
        return cylinderOD;
    }

    public void setCylinderOD(float cylinderOD) {
        this.cylinderOD = cylinderOD;
    }

    public float getAxisOD() {
        return axisOD;
    }

    public void setAxisOD(float axisOD) {
        this.axisOD = axisOD;
    }

    public float getSphereOS() {
        return sphereOS;
    }

    public void setSphereOS(float sphereOS) {
        this.sphereOS = sphereOS;
    }

    public float getCylinderOS() {
        return cylinderOS;
    }

    public void setCylinderOS(float cylinderOS) {
        this.cylinderOS = cylinderOS;
    }

    public float getAxisOS() {
        return axisOS;
    }

    public void setAxisOS(float axisOS) {
        this.axisOS = axisOS;
    }

    public float getAddOD() {
        return addOD;
    }

    public void setAddOD(float addOD) {
        this.addOD = addOD;
    }

    public float getAddOS() {
        return addOS;
    }

    public void setAddOS(float addOS) {
        this.addOS = addOS;
    }

    public int getPd() {
        return pd;
    }

    public void setPd(int pd) {
        this.pd = pd;
    }
}
