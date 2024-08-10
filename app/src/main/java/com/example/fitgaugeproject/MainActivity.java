package com.example.fitgaugeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fitgaugeproject.Models.Gym;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout;
    private Button btnEditPlan;
    private Button btnTrainingPlan;
    private Button btnGymStatus;
    private ImageView main_IMG_image;
    private TextView main_LBL_welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();
        initalizeGyms();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btnTrainingPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Training Plan Activity
                Intent intent = new Intent(MainActivity.this, TrainingPlanActivity.class);
                startActivity(intent);
            }
        });

        btnEditPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Edit My Workouts Activity
                Intent intent = new Intent(MainActivity.this, EditMyWorkoutsActivity.class);
                startActivity(intent);
            }
        });

        btnGymStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Gym Status Activity
                Intent intent = new Intent(MainActivity.this, GymStatusActivity.class);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        main_IMG_image = findViewById(R.id.main_IMG_image);
        main_LBL_welcome = findViewById(R.id.main_LBL_welcome);
        btnLogout = findViewById(R.id.btn_logout);
        btnEditPlan = findViewById(R.id.btn_edit_workouts);
        btnTrainingPlan = findViewById(R.id.btn_training_plan);
        btnGymStatus = findViewById(R.id.btn_gym_status);
    }

    private void initViews() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName() != null ? user.getDisplayName() : "User";
            main_LBL_welcome.setText("Welcome, " + displayName + "!");

            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_user)
                    .into(main_IMG_image);
        }
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // User is now signed out
                        startLoginActivity();
                    }
                });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initalizeGyms() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference gymsRef = database.getReference("allGyms").child("e6edcdaa-4e56-4747-9c1e-307916bdd9c5");

        gymsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Create a new Gym instance
                    Gym newGym = new Gym(
                            "winsport", // Gym name
                            32.1591775, // Latitude
                            34.9737333, // Longitude
                            100, // Number of registered trainees
                            0 // Current number of trainees
                    );

                    // Add the gym to the Firebase database
                    gymsRef.setValue(newGym)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Successfully added gym
                                    Toast.makeText(MainActivity.this, "Gym added successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Failed to add gym
                                    Toast.makeText(MainActivity.this, "Failed to add gym. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Gym already exists in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error checking gym existence: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
