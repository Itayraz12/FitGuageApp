package com.example.fitgaugeproject.Models;


import java.util.HashMap;

public class week {

    private HashMap<String, workout> allWorkouts = new HashMap<>();


    public HashMap<String, workout> getAllWorkouts() {
        return allWorkouts;
    }

    public week setAllWorkouts(HashMap<String, workout> allWorkouts) {
        this.allWorkouts = allWorkouts;
        return this;
    }

    @Override
    public String toString() {
        return "week{" +
                "allWorkouts=" + allWorkouts +
                '}';
    }
}
