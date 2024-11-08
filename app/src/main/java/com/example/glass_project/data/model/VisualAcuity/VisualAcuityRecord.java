package com.example.glass_project.data.model.VisualAcuity;

import java.io.Serializable;

public class VisualAcuityRecord implements Serializable {
    private int id;
    private int profileID;
    private String startDate;
    private boolean status;

    public VisualAcuityRecord(int id, int profileID, String startDate, boolean status) {
        this.id = id;
        this.profileID = profileID;
        this.startDate = startDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getProfileID() {
        return profileID;
    }

    public String getStartDate() {
        return startDate;
    }

    public boolean isStatus() {
        return status;
    }
}
