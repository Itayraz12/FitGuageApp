package com.example.fitgaugeproject;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitgaugeproject.Adapters.WorkoutAdapter;
import com.example.fitgaugeproject.Models.exercise;
import com.example.fitgaugeproject.Models.workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrainingPlanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private ArrayList<workout> workouts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_plan);

        recyclerView = findViewById(R.id.training_plan_recycler_view);
        workoutAdapter = new WorkoutAdapter(workouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(workoutAdapter);

        loadTrainingPlan();
    }

    private void loadTrainingPlan() {
        DatabaseReference planRef = FirebaseDatabase.getInstance().getReference("Training plans")
                .child("Plan 1")
                .child("week of workouts")
                .child("allworkouts");

        planRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workouts.clear(); // Clear the list before adding new data

                // Loop through each workout
                for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                    workout workoutItem = new workout();

                    // Extract workout name
                    String workoutName = workoutSnapshot.getKey();
                    if (workoutName != null) {
                        workoutItem.setWorkoutName(workoutName);
                    }

                    // Extract workout details
                    workout.WorkOutType type = workoutSnapshot.child("type").getValue(workout.WorkOutType.class);
                    workoutItem.setType(type != null ? type : workout.WorkOutType.AEROBIC_TRAINING); // Default to AEROBIC_TRAINING

                    Boolean isDone = workoutSnapshot.child("isDone").getValue(Boolean.class);
                    workoutItem.setIsDone(isDone != null && isDone); // Default to false

                    // Extract exercises
                    DataSnapshot exercisesSnapshot = workoutSnapshot.child("exercises");
                    if (exercisesSnapshot.exists()) {
                        for (DataSnapshot exerciseSnapshot : exercisesSnapshot.getChildren()) {
                            exercise exerciseItem = exerciseSnapshot.getValue(exercise.class);
                            if (exerciseItem != null) {
                                // Get YouTube link and Lottie animation reference
                                String youtubeUrl = exerciseSnapshot.child("youtubeUrl").getValue(String.class);
                                String animationResId = exerciseSnapshot.child("animationResId").getValue(String.class);

                                exerciseItem.setYoutubeUrl(youtubeUrl != null ? youtubeUrl : "");
                                exerciseItem.setAnimationResId(animationResId != null ? animationResId : "");

                                workoutItem.addExercise(exerciseItem);
                            }
                        }
                    }

                    workouts.add(workoutItem);
                }

                workoutAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TrainingPlanActivity", "Failed to load training plan: " + error.getMessage());
            }
        });
    }

}
