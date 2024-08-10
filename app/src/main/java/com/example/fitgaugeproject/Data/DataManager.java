package com.example.fitgaugeproject.Data;

import com.example.fitgaugeproject.Models.Gym;
import com.example.fitgaugeproject.Models.exercise;
import com.example.fitgaugeproject.Models.week;
import com.example.fitgaugeproject.Models.workout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataManager {
    GymManager gymManager = new GymManager();

    public static week createWeekOfWorkouts() {
        week weekOfWorkouts = new week();
        weekOfWorkouts.getAllWorkouts()
                .put("First workout",
                        new workout()
                                .setIsDone(false)
                                .setType(workout.WorkOutType.STRENGTH_TRAINING)
                                .addExercise(new exercise("bench presses", 3, 12)));

        return weekOfWorkouts;
    }

    public void addGymToDatabase() {
        // Create a new Gym instance
        Gym newGym = new Gym(
                "winsport", // Gym name
                32.1591775, // Latitude
                34.9737333, // Longitude
                100, // Number of registered trainees
                0 // Current number of trainees
        );

        // Get a reference to the "allGyms" node in the Firebase database
        DatabaseReference gymsRef = FirebaseDatabase.getInstance().getReference("allGyms");

        // Generate a unique key for the gym
        String gymId = gymsRef.push().getKey();

        // Set the gymId for the new Gym object
        newGym.setGymId(gymId);

        // Add the gym to the Firebase database
        gymsRef.child(gymId).setValue(newGym)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully added gym
                        System.out.println("Gym added successfully.");
                    } else {
                        // Failed to add gym
                        System.out.println("Failed to add gym.");
                    }
                });
    }
}
