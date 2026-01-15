package com.stackprotocol.capsule_temporelle.dto;

import java.time.LocalDate;

public class TimeCapsuleResume {
    private String id;
    private LocalDate launchDate;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public boolean isLaunched() {
        return LocalDate.now().isAfter(launchDate);
    }
}
