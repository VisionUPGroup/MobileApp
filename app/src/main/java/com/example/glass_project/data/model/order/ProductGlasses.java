package com.example.glass_project.data.model.order;

public class ProductGlasses {
    private int eyeGlassID;
    private int leftLenID;
    private int rightLenID;
    private int accountID;
    private int total;
    private int sphereOD;
    private int cylinderOD;
    private int axisOD;
    private int sphereOS;
    private int cylinderOS;
    private int axisOS;
    private int addOD;
    private int addOS;
    private int pd;


    public ProductGlasses(int eyeGlassID, int leftLenID, int rightLenID, int accountID, int total, int sphereOD, int cylinderOD, int axisOD, int sphereOS, int cylinderOS, int axisOS, int addOD, int addOS, int pd) {
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

    public int getSphereOD() {
        return sphereOD;
    }

    public void setSphereOD(int sphereOD) {
        this.sphereOD = sphereOD;
    }

    public int getCylinderOD() {
        return cylinderOD;
    }

    public void setCylinderOD(int cylinderOD) {
        this.cylinderOD = cylinderOD;
    }

    public int getAxisOD() {
        return axisOD;
    }

    public void setAxisOD(int axisOD) {
        this.axisOD = axisOD;
    }

    public int getSphereOS() {
        return sphereOS;
    }

    public void setSphereOS(int sphereOS) {
        this.sphereOS = sphereOS;
    }

    public int getCylinderOS() {
        return cylinderOS;
    }

    public void setCylinderOS(int cylinderOS) {
        this.cylinderOS = cylinderOS;
    }

    public int getAxisOS() {
        return axisOS;
    }

    public void setAxisOS(int axisOS) {
        this.axisOS = axisOS;
    }

    public int getAddOD() {
        return addOD;
    }

    public void setAddOD(int addOD) {
        this.addOD = addOD;
    }

    public int getAddOS() {
        return addOS;
    }

    public void setAddOS(int addOS) {
        this.addOS = addOS;
    }

    public int getPd() {
        return pd;
    }

    public void setPd(int pd) {
        this.pd = pd;
    }


}
