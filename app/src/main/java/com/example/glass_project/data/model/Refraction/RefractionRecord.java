package com.example.glass_project.data.model.Refraction;

import java.util.List;

public class RefractionRecord {
    private int id;
    private int profileID;
    private String startTime;
    private boolean status;
    private List<com.example.glass_project.data.model.Refraction.Kiosk> kiosks;
    private boolean isJoin;

    public RefractionRecord(int id, int profileID, String startTime, boolean status, List<Kiosk> kiosks, boolean isJoin) {
        this.id = id;
        this.profileID = profileID;
        this.startTime = startTime;
        this.status = status;
        this.kiosks = kiosks;
        this.isJoin = isJoin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Kiosk> getKiosks() {
        return kiosks;
    }

    public void setKiosks(List<Kiosk> kiosks) {
        this.kiosks = kiosks;
    }

    public boolean isJoin() {
        return isJoin;
    }

    public void setJoin(boolean join) {
        isJoin = join;
    }
}
