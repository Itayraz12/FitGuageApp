package com.example.fitgaugeproject;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GymStatusActivity extends AppCompatActivity {

    private TextView gymNameTextView;
    private TextView gymTraineesTextView;
    private TextView gymTraineesLargeTextView;
    private LottieAnimationView lottieGauge;
    private Long previousTraineesCount = null;

    private static final String TAG = "GymStatusActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_status);

        // Initialize UI components
        gymNameTextView = findViewById(R.id.gym_name);
        gymTraineesTextView = findViewById(R.id.gym_trainees);
        gymTraineesLargeTextView = findViewById(R.id.gym_trainees_large);
        lottieGauge = findViewById(R.id.lottie_gauge);

        // Set listener to know when the animation ends
        lottieGauge.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                lottieGauge.setVisibility(View.GONE);
                gymTraineesLargeTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        // Load gym data from Firebase
        loadGymData();
    }

    private void loadGymData() {
        // Get the current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the user's gymId in Firebase
        DatabaseReference userGymRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("gymId");

        userGymRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String gymId = snapshot.getValue(String.class);
                Log.d(TAG, "User's gymId: " + gymId);

                // Load gym details if gymId is not null
                if (gymId != null) {
                    loadGymDetails(gymId);
                } else {
                    Log.e(TAG, "Gym ID is null. Check if the user has a gym ID associated.");
                    gymNameTextView.setText("Gym not found for this user.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch user's gym ID: " + error.getMessage());
            }
        });
    }

    private void loadGymDetails(String gymId) {
        // Reference to the specific gym's data in Firebase
        DatabaseReference gymRef = FirebaseDatabase.getInstance().getReference("allGyms").child(gymId);

        gymRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Logging the entire snapshot
                    Log.d(TAG, "Gym Data Snapshot: " + snapshot.getValue());

                    String gymName = snapshot.child("gymName").getValue(String.class);
                    Long currentTrainees = snapshot.child("currentNumberOfTrainees").getValue(Long.class);

                    if (gymName != null) {
                        gymNameTextView.setText(gymName);
                    } else {
                        gymNameTextView.setText("Gym Name not found");
                    }

                    if (currentTrainees != null) {
                        gymTraineesTextView.setText("Current Trainees:");
                        gymTraineesLargeTextView.setText(String.valueOf(currentTrainees));
                    } else {
                        Log.e(TAG, "currentNumberOfTrainees is null");
                        gymTraineesTextView.setText("Current Trainees: 0");
                        gymTraineesLargeTextView.setText("0");
                    }
                } else {
                    Log.e(TAG, "Snapshot does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch gym details: " + error.getMessage());
            }
        });
    }



}

