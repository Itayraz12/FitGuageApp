package com.example.fitgaugeproject.Models;

import java.util.UUID;

public class Gym {
    private String gymId;
    private String gymName;
    private double latitude;
    private double longitude;
    private int numberOfRegisteredTrainees;
    private int currentNumberOfTrainees;

    // No-argument constructor
    public Gym() {
    }


    // Constructor for creating a new gym with generated gymId
    public Gym(String gymName, double latitude, double longitude, int numberOfRegisteredTrainees, int currentNumberOfTrainees) {
        this.gymId = UUID.randomUUID().toString(); // Generate a unique ID
        this.gymName = gymName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numberOfRegisteredTrainees = numberOfRegisteredTrainees;
        this.currentNumberOfTrainees = currentNumberOfTrainees;
    }

    // Getters and Setters
    public String getGymId() {
        return gymId;
    }

    public void setGymId(String gymId) {
        this.gymId = gymId;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getNumberOfRegisteredTrainees() {
        return numberOfRegisteredTrainees;
    }

    public void setNumberOfRegisteredTrainees(int numberOfRegisteredTrainees) {
        this.numberOfRegisteredTrainees = numberOfRegisteredTrainees;
    }

    public int getCurrentNumberOfTrainees() {
        return currentNumberOfTrainees;
    }

    public void setCurrentNumberOfTrainees(int currentNumberOfTrainees) {
        this.currentNumberOfTrainees = currentNumberOfTrainees;
    }

    @Override
    public String toString() {
        return "Gym{" +
                "gymId='" + gymId + '\'' +
                ", gymName='" + gymName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", numberOfRegisteredTrainees=" + numberOfRegisteredTrainees +
                ", currentNumberOfTrainees=" + currentNumberOfTrainees +
                '}';
    }
}
