package com.example.glass_project.data.model.Refraction;

public class MeasurementResult {
    private int id;
    private int recordID;
    private int employeeID;
    private int testType;
    private double spherical;
    private double cylindrical;
    private double axis;
    private double pupilDistance;
    private int eyeSide;
    private String prescriptionDetails;
    private String lastCheckupDate;
    private String nextCheckupDate;
    private String notes;

    public MeasurementResult(int id, int recordID, int employeeID, int testType, double spherical,
                             double cylindrical, double axis, double pupilDistance, int eyeSide,
                             String prescriptionDetails, String lastCheckupDate, String nextCheckupDate, String notes) {
        this.id = id;
        this.recordID = recordID;
        this.employeeID = employeeID;
        this.testType = testType;
        this.spherical = spherical;
        this.cylindrical = cylindrical;
        this.axis = axis;
        this.pupilDistance = pupilDistance;
        this.eyeSide = eyeSide;
        this.prescriptionDetails = prescriptionDetails;
        this.lastCheckupDate = lastCheckupDate;
        this.nextCheckupDate = nextCheckupDate;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public int getTestType() {
        return testType;
    }

    public void setTestType(int testType) {
        this.testType = testType;
    }

    public double getSpherical() {
        return spherical;
    }

    public void setSpherical(double spherical) {
        this.spherical = spherical;
    }

    public double getCylindrical() {
        return cylindrical;
    }

    public void setCylindrical(double cylindrical) {
        this.cylindrical = cylindrical;
    }

    public double getAxis() {
        return axis;
    }

    public void setAxis(double axis) {
        this.axis = axis;
    }

    public double getPupilDistance() {
        return pupilDistance;
    }

    public void setPupilDistance(double pupilDistance) {
        this.pupilDistance = pupilDistance;
    }

    public int getEyeSide() {
        return eyeSide;
    }

    public void setEyeSide(int eyeSide) {
        this.eyeSide = eyeSide;
    }

    public String getPrescriptionDetails() {
        return prescriptionDetails;
    }

    public void setPrescriptionDetails(String prescriptionDetails) {
        this.prescriptionDetails = prescriptionDetails;
    }

    public String getLastCheckupDate() {
        return lastCheckupDate;
    }

    public void setLastCheckupDate(String lastCheckupDate) {
        this.lastCheckupDate = lastCheckupDate;
    }

    public String getNextCheckupDate() {
        return nextCheckupDate;
    }

    public void setNextCheckupDate(String nextCheckupDate) {
        this.nextCheckupDate = nextCheckupDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
