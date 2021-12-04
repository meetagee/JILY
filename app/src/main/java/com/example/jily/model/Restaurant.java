package com.example.jily.model;

public class Restaurant {

    private String name;
    private String status;

    public Restaurant(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }

    public void setStatus(String date) { this.status = date; }
}