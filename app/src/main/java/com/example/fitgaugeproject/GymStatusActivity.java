package com.example.fitgaugeproject;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GymStatusActivity extends AppCompatActivity {

    private static final String TAG = "GymStatusActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private TextView gymNameTextView;
    private TextView gymTraineesTextView;
    private TextView gymTraineesLargeTextView;
    private LottieAnimationView lottieGauge;
    private Button navigateButton;
    private Double gymLatitude;
    private Double gymLongitude;
    private Long previousTraineesCount = null;

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
        navigateButton = findViewById(R.id.navigate_button);

        // Request location permissions if not already granted
        requestLocationPermission();

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

        // Set up click listener for navigation button
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gymLatitude != null && gymLongitude != null) {
                    openGoogleMaps(gymLatitude, gymLongitude);
                } else {
                    Log.e(TAG, "Gym location is not available.");
                }
            }
        });
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted.");
            } else {
                Log.e(TAG, "Location permission denied.");
            }
        }
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
        DatabaseReference gymRef = FirebaseDatabase.getInstance().getReference("allGyms");

        // Query the gyms node to find the gym with the given gymId
        gymRef.orderByChild("gymId").equalTo(gymId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot gymSnapshot : snapshot.getChildren()) {
                        // Logging the entire snapshot
                        Log.d(TAG, "Gym Data Snapshot: " + gymSnapshot.getValue());

                        String gymName = gymSnapshot.child("gymName").getValue(String.class);
                        Long currentTrainees = gymSnapshot.child("currentNumberOfTrainees").getValue(Long.class);
                        gymLatitude = gymSnapshot.child("latitude").getValue(Double.class);
                        gymLongitude = gymSnapshot.child("longitude").getValue(Double.class);

                        if (gymName != null) {
                            gymNameTextView.setText(gymName);
                        } else {
                            gymNameTextView.setText("Gym Name not found");
                        }

                        if (currentTrainees != null) {
                            gymTraineesTextView.setText("Current Trainees: ");
                            gymTraineesLargeTextView.setText(String.valueOf(currentTrainees));
                        } else {
                            Log.e(TAG, "currentNumberOfTrainees is null");
                            gymTraineesTextView.setText("Current Trainees:");
                            gymTraineesLargeTextView.setText("0");
                        }
                    }
                } else {
                    Log.e(TAG, "Gym with the specified ID does not exist.");
                    gymNameTextView.setText("Gym not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch gym details: " + error.getMessage());
            }
        });
    }



    private boolean isGoogleMapsInstalled() {
        try {
            getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            Log.d(TAG, "Google Maps is installed.");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Google Maps is not installed.");
            return false;
        }
    }

    private void openGoogleMaps(double latitude, double longitude) {
        // Create the URI for navigation
        String uri = "google.navigation:q=" + latitude + "," + longitude;

        // Create an intent to launch Google Maps
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        // Check if there's an app available to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "No app available to handle geo intent.");
            // Optional: Redirect to Google Play Store to install Google Maps
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
            startActivity(playStoreIntent);
        }
    }

}
