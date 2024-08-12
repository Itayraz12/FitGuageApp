package com.example.fitgaugeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitgaugeproject.Data.DataManager;
import com.example.fitgaugeproject.Models.exercise;
import com.example.fitgaugeproject.Models.week;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditMyWorkoutsActivity extends AppCompatActivity {

    private Spinner main_SP_workouts;
    private ArrayList<String> workoutNames = new ArrayList<>();
    private TextInputEditText main_ET_text_name;
    private MaterialTextView main_LBL_title;
    private MaterialButton main_BTN_back;
    private MaterialButton main_BTN_submit;
    private TextInputEditText main_ET_text_sets;
    private TextInputEditText main_ET_text_repetitions;
    private MaterialTextView main_LBL_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_workouts);

        findViews(); // Initialize all view references
        loadWorkoutNames(); // Load workout names from Firebase
        initViews(); // Set up click listeners
        updateTitleFromDB(); // Update title from Firebase
    }

    private void updateTitleFromDB() {
        DatabaseReference titleRef = FirebaseDatabase.getInstance().getReference("title");

        titleRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        main_LBL_title.setText(value);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle possible errors
                    }
                });
    }

    private void loadWorkoutNames() {
        DatabaseReference workoutsRef = FirebaseDatabase.getInstance()
                .getReference("Training plans")
                .child("Plan 1")
                .child("week of workouts")
                .child("allworkouts");

        workoutsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workoutNames.clear(); // Clear the list before adding new data
                for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                    String workoutName = workoutSnapshot.getKey();
                    workoutNames.add(workoutName);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditMyWorkoutsActivity.this, android.R.layout.simple_spinner_item, workoutNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                main_SP_workouts.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void initViews() {
        main_BTN_submit.setOnClickListener(v -> {
            String selectedWorkout = main_SP_workouts.getSelectedItem().toString();
            updateWorkout(selectedWorkout);
        });
        main_BTN_back.setOnClickListener(v -> {
            // Navigate back to MainActivity
            Intent intent = new Intent(EditMyWorkoutsActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional: Call finish() if you don't want this activity to remain in the back stack.
        });
    }


    private void loadDataAndShowOnScreen() {
        DatabaseReference garageRef = FirebaseDatabase.getInstance().getReference("garage");
        garageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                week week = snapshot.getValue(week.class);
                assert week != null;
                main_LBL_data.setText(week.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void updateWorkout(String selectedWorkout) {
        // Get the exercise details from the text fields
        String exerciseName = main_ET_text_name.getText().toString();
        int numberOfSets = Integer.parseInt(main_ET_text_sets.getText().toString());
        int numberOfRepetitions = Integer.parseInt(main_ET_text_repetitions.getText().toString());

        // Create a new exercise object
        exercise newExercise = new exercise();
        newExercise.setExerciseName(exerciseName);
        newExercise.setNumberOfSets(numberOfSets);
        newExercise.setNumberOfRepetitions(numberOfRepetitions);

        // Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference exercisesRef = database.getReference("Training plans")
                .child("Plan 1")
                .child("week of workouts")
                .child("allworkouts")
                .child(selectedWorkout)
                .child("exercises");

        // Get the current number of exercises to determine the next index
        exercisesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentExerciseCount = snapshot.getChildrenCount();
                exercisesRef.child(String.valueOf(currentExerciseCount))
                        .setValue(newExercise)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successfully added exercise
                                Toast.makeText(EditMyWorkoutsActivity.this, "Exercise added successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Failed to add exercise
                                Toast.makeText(EditMyWorkoutsActivity.this, "Failed to add exercise. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void saveDataToFirebase() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference workoutsRef = database.getReference("Training plans")
                .child("Plan 1")
                .child("week of workouts");

        workoutsRef.setValue(DataManager.createWeekOfWorkouts());
    }

    private void findViews() {
        main_BTN_submit = findViewById(R.id.main_BTN_updateWorkout);
        main_SP_workouts = findViewById(R.id.main_SP_workouts);
        main_ET_text_name = findViewById(R.id.main_ET_text_name);
        main_LBL_title = findViewById(R.id.main_LBL_title);
        main_BTN_back = findViewById(R.id.main_BTN_back);
        main_ET_text_sets = findViewById(R.id.main_ET_text_sets);
        main_ET_text_repetitions = findViewById(R.id.main_ET_text_repetitions);
        main_LBL_data = findViewById(R.id.main_LBL_data);
    }
}
