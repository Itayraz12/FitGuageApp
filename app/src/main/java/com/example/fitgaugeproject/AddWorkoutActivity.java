package com.example.fitgaugeproject;

import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddWorkoutActivity extends AppCompatActivity {

    private EditText editWorkoutName;
    private Spinner spinnerWorkoutType;
    private LinearLayout exercisesContainer;
    private Button btnAddExercise;
    private Button btnSaveWorkout;

    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Training plans");

        // Find views
        editWorkoutName = findViewById(R.id.edit_workout_name);
        spinnerWorkoutType = findViewById(R.id.spinner_workout_type);
        exercisesContainer = findViewById(R.id.exercises_container);
        btnAddExercise = findViewById(R.id.btn_add_exercise);
        btnSaveWorkout = findViewById(R.id.btn_save_workout);

        // Set up Spinner for workout types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.workout_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkoutType.setAdapter(adapter);

        // Add new exercise field on button click
        btnAddExercise.setOnClickListener(v -> addExerciseField());

        // Save the workout on button click
        btnSaveWorkout.setOnClickListener(v -> saveWorkout());
    }

    private void addExerciseField() {
        LinearLayout exerciseLayout = new LinearLayout(this);
        exerciseLayout.setOrientation(LinearLayout.VERTICAL);

        EditText exerciseName = new EditText(this);
        exerciseName.setHint("Enter exercise name");
        exerciseName.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        exerciseName.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        EditText numberOfSets = new EditText(this);
        numberOfSets.setHint("Enter number of sets");
        numberOfSets.setInputType(InputType.TYPE_CLASS_NUMBER);
        numberOfSets.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        numberOfSets.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        EditText numberOfRepetitions = new EditText(this);
        numberOfRepetitions.setHint("Enter number of repetitions");
        numberOfRepetitions.setInputType(InputType.TYPE_CLASS_NUMBER);
        numberOfRepetitions.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        numberOfRepetitions.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        EditText youtubeUrl = new EditText(this);
        youtubeUrl.setHint("Enter YouTube URL");
        youtubeUrl.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        youtubeUrl.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        EditText animationResId = new EditText(this);
        animationResId.setHint("Enter animation resource ID");
        animationResId.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        animationResId.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        exerciseLayout.addView(exerciseName);
        exerciseLayout.addView(numberOfSets);
        exerciseLayout.addView(numberOfRepetitions);
        exerciseLayout.addView(youtubeUrl);
        exerciseLayout.addView(animationResId);

        exercisesContainer.addView(exerciseLayout);
    }


    private void saveWorkout() {
        String workoutName = editWorkoutName.getText().toString();
        String workoutType = spinnerWorkoutType.getSelectedItem().toString();

        if (workoutName.isEmpty()) {
            Toast.makeText(this, "Please enter a workout name", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        // Find the training plan for the current user
        database.orderByChild("PlanOwner").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Assuming there's only one plan per user
                    for (DataSnapshot planSnapshot : snapshot.getChildren()) {
                        DatabaseReference workoutsRef = planSnapshot.child("week of workouts").child("allworkouts").getRef();

                        String workoutId = workoutsRef.push().getKey();

                        // Create a map to store workout details
                        Map<String, Object> workoutData = new HashMap<>();
                        workoutData.put("name", workoutName);
                        workoutData.put("type", workoutType);
                        workoutData.put("isDone", false);

                        // Create a map to store exercises
                        Map<String, Object> exercisesMap = new HashMap<>();
                        for (int i = 0; i < exercisesContainer.getChildCount(); i++) {
                            LinearLayout exerciseLayout = (LinearLayout) exercisesContainer.getChildAt(i);

                            EditText exerciseNameField = (EditText) exerciseLayout.getChildAt(0);
                            EditText numberOfSetsField = (EditText) exerciseLayout.getChildAt(1);
                            EditText numberOfRepetitionsField = (EditText) exerciseLayout.getChildAt(2);
                            EditText youtubeUrlField = (EditText) exerciseLayout.getChildAt(3);
                            EditText animationResIdField = (EditText) exerciseLayout.getChildAt(4);

                            String exerciseName = exerciseNameField.getText().toString();
                            String numberOfSets = numberOfSetsField.getText().toString();
                            String numberOfRepetitions = numberOfRepetitionsField.getText().toString();
                            String youtubeUrl = youtubeUrlField.getText().toString();
                            String animationResId = animationResIdField.getText().toString();

                            if (!exerciseName.isEmpty()) {
                                Map<String, Object> exerciseData = new HashMap<>();
                                exerciseData.put("exerciseName", exerciseName);
                                exerciseData.put("numberOfSets", Integer.parseInt(numberOfSets));
                                exerciseData.put("numberOfRepetitions", Integer.parseInt(numberOfRepetitions));
                                exerciseData.put("youtubeUrl", youtubeUrl);
                                exerciseData.put("animationResId", animationResId);

                                exercisesMap.put(String.valueOf(i + 1), exerciseData);
                            }
                        }

                        workoutData.put("exercises", exercisesMap);

                        // Add workout to the Firebase database
                        workoutsRef.child(workoutName).setValue(workoutData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddWorkoutActivity.this, "Workout added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddWorkoutActivity.this, "Failed to add workout", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(AddWorkoutActivity.this, "No training plan found for this user.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AddWorkoutActivity.this, "Failed to find training plan.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
