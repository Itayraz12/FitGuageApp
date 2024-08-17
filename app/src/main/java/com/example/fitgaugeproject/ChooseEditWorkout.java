package com.example.fitgaugeproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseEditWorkout extends AppCompatActivity {

    private Button btnAddWorkout;
    private Button btnEditWorkout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_edit_workout);

        // Find views
        btnAddWorkout = findViewById(R.id.btn_add_workout);
        btnEditWorkout = findViewById(R.id.btn_edit_workout);

        // Set click listeners
        btnAddWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseEditWorkout.this, AddWorkoutActivity.class);
            startActivity(intent);
        });

        btnEditWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseEditWorkout.this, EditMyWorkoutsActivity.class);
            startActivity(intent);
        });
    }
}
