package com.example.glass_project.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductGlass implements Serializable {
    private int id;
    private int eyeGlassID;
    private int leftLenID;
    private int rightLenID;
    private Integer profileMeasurementID; // NULLable field
    private BigDecimal sphereOD; // NULLable field
    private BigDecimal cylinderOD; // NULLable field
    private Integer axisOD; // NULLable field
    private BigDecimal sphereOS; // NULLable field
    private BigDecimal cylinderOS; // NULLable field
    private Integer axisOS; // NULLable field
    private BigDecimal addOD; // NULLable field
    private BigDecimal addOS; // NULLable field
    private BigDecimal pd; // NULLable field
    private boolean status;
    private int accountID;
    private BigDecimal total;

    public ProductGlass(int id, int eyeGlassID, int leftLenID, int rightLenID, Integer profileMeasurementID, BigDecimal sphereOD, BigDecimal cylinderOD, Integer axisOD, BigDecimal sphereOS, BigDecimal cylinderOS, Integer axisOS, BigDecimal addOD, BigDecimal addOS, BigDecimal pd, boolean status, int accountID, BigDecimal total) {
        this.id = id;
        this.eyeGlassID = eyeGlassID;
        this.leftLenID = leftLenID;
        this.rightLenID = rightLenID;
        this.profileMeasurementID = profileMeasurementID;
        this.sphereOD = sphereOD;
        this.cylinderOD = cylinderOD;
        this.axisOD = axisOD;
        this.sphereOS = sphereOS;
        this.cylinderOS = cylinderOS;
        this.axisOS = axisOS;
        this.addOD = addOD;
        this.addOS = addOS;
        this.pd = pd;
        this.status = status;
        this.accountID = accountID;
        this.total = total;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Integer getProfileMeasurementID() {
        return profileMeasurementID;
    }

    public void setProfileMeasurementID(Integer profileMeasurementID) {
        this.profileMeasurementID = profileMeasurementID;
    }

    public BigDecimal getSphereOD() {
        return sphereOD;
    }

    public void setSphereOD(BigDecimal sphereOD) {
        this.sphereOD = sphereOD;
    }

    public BigDecimal getCylinderOD() {
        return cylinderOD;
    }

    public void setCylinderOD(BigDecimal cylinderOD) {
        this.cylinderOD = cylinderOD;
    }

    public Integer getAxisOD() {
        return axisOD;
    }

    public void setAxisOD(Integer axisOD) {
        this.axisOD = axisOD;
    }

    public BigDecimal getSphereOS() {
        return sphereOS;
    }

    public void setSphereOS(BigDecimal sphereOS) {
        this.sphereOS = sphereOS;
    }

    public BigDecimal getCylinderOS() {
        return cylinderOS;
    }

    public void setCylinderOS(BigDecimal cylinderOS) {
        this.cylinderOS = cylinderOS;
    }

    public Integer getAxisOS() {
        return axisOS;
    }

    public void setAxisOS(Integer axisOS) {
        this.axisOS = axisOS;
    }

    public BigDecimal getAddOD() {
        return addOD;
    }

    public void setAddOD(BigDecimal addOD) {
        this.addOD = addOD;
    }

    public BigDecimal getAddOS() {
        return addOS;
    }

    public void setAddOS(BigDecimal addOS) {
        this.addOS = addOS;
    }

    public BigDecimal getPd() {
        return pd;
    }

    public void setPd(BigDecimal pd) {
        this.pd = pd;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
