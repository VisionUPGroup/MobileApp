package com.example.glass_project.data.model.exchange;

public class ProductGlass {
    private int id;
    private EyeGlass eyeGlass;
    private Lens leftLen;
    private Lens rightLen;
    private Double sphereOD;
    private Double cylinderOD;
    private Integer axisOD;
    private Double sphereOS;
    private Double cylinderOS;
    private Integer axisOS;
    private Double addOD;
    private Double addOS;
    private Integer pd;
    private int total;
    private int quantity;
    private boolean status;

    public ProductGlass(int id, EyeGlass eyeGlass, Lens leftLen, Lens rightLen, Double sphereOD, Double cylinderOD, Integer axisOD, Double sphereOS, Double cylinderOS, Integer axisOS, Double addOD, Double addOS, Integer pd, int total, int quantity, boolean status) {
        this.id = id;
        this.eyeGlass = eyeGlass;
        this.leftLen = leftLen;
        this.rightLen = rightLen;
        this.sphereOD = sphereOD;
        this.cylinderOD = cylinderOD;
        this.axisOD = axisOD;
        this.sphereOS = sphereOS;
        this.cylinderOS = cylinderOS;
        this.axisOS = axisOS;
        this.addOD = addOD;
        this.addOS = addOS;
        this.pd = pd;
        this.total = total;
        this.quantity = quantity;
        this.status = status;
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

    public Lens getLeftLen() {
        return leftLen;
    }

    public void setLeftLen(Lens leftLen) {
        this.leftLen = leftLen;
    }

    public Lens getRightLen() {
        return rightLen;
    }

    public void setRightLen(Lens rightLen) {
        this.rightLen = rightLen;
    }

    public Double getSphereOD() {
        return sphereOD;
    }

    public void setSphereOD(Double sphereOD) {
        this.sphereOD = sphereOD;
    }

    public Double getCylinderOD() {
        return cylinderOD;
    }

    public void setCylinderOD(Double cylinderOD) {
        this.cylinderOD = cylinderOD;
    }

    public Integer getAxisOD() {
        return axisOD;
    }

    public void setAxisOD(Integer axisOD) {
        this.axisOD = axisOD;
    }

    public Double getSphereOS() {
        return sphereOS;
    }

    public void setSphereOS(Double sphereOS) {
        this.sphereOS = sphereOS;
    }

    public Double getCylinderOS() {
        return cylinderOS;
    }

    public void setCylinderOS(Double cylinderOS) {
        this.cylinderOS = cylinderOS;
    }

    public Integer getAxisOS() {
        return axisOS;
    }

    public void setAxisOS(Integer axisOS) {
        this.axisOS = axisOS;
    }

    public Double getAddOD() {
        return addOD;
    }

    public void setAddOD(Double addOD) {
        this.addOD = addOD;
    }

    public Double getAddOS() {
        return addOS;
    }

    public void setAddOS(Double addOS) {
        this.addOS = addOS;
    }

    public Integer getPd() {
        return pd;
    }

    public void setPd(Integer pd) {
        this.pd = pd;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
