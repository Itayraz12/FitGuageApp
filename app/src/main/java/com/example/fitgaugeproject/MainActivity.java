package com.example.fitgaugeproject;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.fitgaugeproject.Models.Gym;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = "MainActivity";

    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;

    private Button btnLogout;
    private Button btnEditPlan;
    private Button btnTrainingPlan;
    private Button btnGymStatus;
    private ImageView main_IMG_image;
    private TextView main_LBL_welcome;


    private static final String PREFS_NAME = "FitGaugePrefs";
    private static final String KEY_WORKOUT_DIALOG_SHOWN_V1 = "workoutDialogShown_v1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearSharedPreferences(); // Temporary for testing
        findViews();
        initViews();

        // Check and show the workout alert dialog
        showWorkoutAlertDialogIfNecessary();

        btnLogout.setOnClickListener(v -> logout());

        btnTrainingPlan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrainingPlanActivity.class);
            startActivity(intent);
        });

        btnEditPlan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditMyWorkoutsActivity.class);
            startActivity(intent);
        });

        btnGymStatus.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GymStatusActivity.class);
            startActivity(intent);
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

    private void showWorkoutAlertDialogIfNecessary() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean dialogShown = prefs.getBoolean(KEY_WORKOUT_DIALOG_SHOWN_V1, false);

        if (!dialogShown) {
            showWorkoutAlertDialog();
            prefs.edit().putBoolean(KEY_WORKOUT_DIALOG_SHOWN_V1, true).apply();
        }
    }

    private void clearSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
        Log.d(TAG, "SharedPreferences cleared.");
    }



    private void showWorkoutAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Workout")
                .setMessage("Are you starting a workout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Handle positive response
                    incrementTraineesInGym();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Handle negative response
                    Toast.makeText(this, "No workout started.", Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false) // Prevent dialog from being dismissed by tapping outside
                .show();
    }

    private void incrementTraineesInGym() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Retrieve the user's gymId
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userGymId = snapshot.child("gymId").getValue(String.class);
                    if (userGymId != null && !userGymId.isEmpty()) {
                        DatabaseReference gymsRef = FirebaseDatabase.getInstance().getReference("allGyms");

                        // Query the gyms to find the one with the matching gymId
                        gymsRef.orderByChild("gymId").equalTo(userGymId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // Assuming there's only one gym with this gymId
                                            for (DataSnapshot gymSnapshot : snapshot.getChildren()) {
                                                DatabaseReference gymRef = gymSnapshot.getRef().child("currentNumberOfTrainees");

                                                // Increment the currentNumberOfTrainees
                                                gymRef.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        Integer currentValue = currentData.getValue(Integer.class);
                                                        if (currentValue == null) {
                                                            currentData.setValue(1);
                                                        } else {
                                                            currentData.setValue(currentValue + 1);
                                                        }
                                                        return Transaction.success(currentData);
                                                    }

                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot currentData) {
                                                        if (committed) {
                                                            Toast.makeText(MainActivity.this, "Trainee count updated.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "Failed to update trainee count.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "Gym not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MainActivity.this, "Error loading gym data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(MainActivity.this, "No gym associated with user.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error loading user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }




    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startLoginActivity();
                    }
                });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupGeofencing() {
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceList = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userGymId = snapshot.child("gymId").getValue(String.class);
                    if (userGymId != null && !userGymId.isEmpty()) {
                        setupGeofenceForUserGym(userGymId);
                    } else {
                        Toast.makeText(MainActivity.this, "No gym associated with user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error loading user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupGeofenceForUserGym(String gymId) {
        DatabaseReference gymsRef = FirebaseDatabase.getInstance().getReference("allGyms");

        gymsRef.orderByChild("gymId").equalTo(gymId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot gymSnapshot : snapshot.getChildren()) {
                                Gym gym = gymSnapshot.getValue(Gym.class);
                                if (gym != null) {
                                    geofenceList.add(new Geofence.Builder()
                                            .setRequestId(gym.getGymId()) // Unique ID for each geofence
                                            .setCircularRegion(
                                                    gym.getLatitude(),
                                                    gym.getLongitude(),
                                                    200 // Radius in meters
                                            )
                                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                            .build());

                                    // Add geofence to the geofencing client
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        addGeofences();
                                    } else {
                                        addGeofencesForPreQ();
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Gym not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Error loading gym data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void addGeofences() {
        if (geofenceList.isEmpty()) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if they are not granted
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, REQUEST_LOCATION_PERMISSION);
            return;
        }

        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Geofences added successfully", Toast.LENGTH_SHORT).show();
                    Log.d("GeofenceBroadcastReceiver", "Geofencing successful: " + getGeofencingRequest() + getGeofencePendingIntent());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to add geofences", Toast.LENGTH_SHORT).show();
                    Log.e("GeofenceBroadcastReceiver", "Failed to add geofences", e);
                });
    }

    // For devices with API level < Q
    private void addGeofencesForPreQ() {
        if (geofenceList.isEmpty()) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if they are not granted
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_LOCATION_PERMISSION);
            return;
        }

        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Geofences added successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Geofencing successful: " + getGeofencingRequest() + getGeofencePendingIntent());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to add geofences", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to add geofences", e);
                });
    }

    private GeofencingRequest getGeofencingRequest() {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofenceList)
                .build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        intent.setAction("com.example.fitgaugeproject.ACTION_GEOFENCE_EVENT"); // Explicit action
        geofencePendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return geofencePendingIntent;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    addGeofences();
                } else {
                    addGeofencesForPreQ();
                }
            } else {
                Toast.makeText(this, "Location permissions are required for geofencing", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestIgnoreBatteryOptimizations() {
        Intent intent = new Intent();
        String packageName = getPackageName();

        if (!Settings.canDrawOverlays(this)) {
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(intent);
        } else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }
}