package com.example.fitgaugeproject.Data;

import com.example.fitgaugeproject.Models.Gym;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GymManager {
    private FirebaseDatabase database;
    private DatabaseReference gymsRef;

    public GymManager() {
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        gymsRef = database.getReference("allGyms");
    }

    public void addGym(Gym gym) {
        // Use the gymId as the key for each gym
        gymsRef.child(gym.getGymId()).setValue(gym)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Gym added successfully
                        System.out.println("Gym added successfully!");
                    } else {
                        // Failed to add gym
                        System.out.println("Failed to add gym. Please try again.");
                    }
                });
    }
}
