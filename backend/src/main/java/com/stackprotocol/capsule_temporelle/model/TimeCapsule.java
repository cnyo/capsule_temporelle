package com.stackprotocol.capsule_temporelle.model;

import java.time.LocalDate;
import java.util.UUID;

public class TimeCapsule {
    private String id;
    private String message;

    private LocalDate createdDate;

    private LocalDate launchDate;

    public TimeCapsule() {
        id = UUID.randomUUID().toString();
        createdDate = LocalDate.now();
    }

    public TimeCapsule(String message, LocalDate launchDate) {
        this.message = message;
        this.launchDate = launchDate;
        id = UUID.randomUUID().toString();
        createdDate = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }
}
