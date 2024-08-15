package com.example.fitgaugeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitgaugeproject.Models.Gym;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            signIn();
        } else {
            transactToMainActivity();
        }
    }

    private void transactToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.app_logo)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                checkAndCreateGym(user);
            }
        } else {
            // Handle sign-in failure
        }
    }

    private void checkAndCreateGym(FirebaseUser user) {
        DatabaseReference gymsRef = FirebaseDatabase.getInstance().getReference("allGyms");

        gymsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    // No gyms exist, so create a new gym
                    createNewGym(user);
                } else {
                    // Gyms exist, associate the user with the first gym's actual gymId
                    DataSnapshot firstGymSnapshot = snapshot.getChildren().iterator().next();
                    String actualGymId = firstGymSnapshot.child("gymId").getValue(String.class);
                    saveUserGymAssociation(user.getUid(), actualGymId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Failed to check gyms: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void createNewGym(FirebaseUser user) {
        String gymId = UUID.randomUUID().toString();
        Gym newGym = new    Gym(
                "New Gym", // Gym name
                32.1591775, // Latitude
                34.9737333, // Longitude
                100, // Number of registered trainees
                0 // Current number of trainees
        );

        DatabaseReference gymsRef = FirebaseDatabase.getInstance().getReference("allGyms").child(gymId);
        gymsRef.setValue(newGym).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Successfully added gym, associate it with the user
                saveUserGymAssociation(user.getUid(), gymId);
                Toast.makeText(LoginActivity.this, "New Gym created and associated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Failed to create Gym. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserGymAssociation(String userId, String gymId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("gymId").setValue(gymId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Gym ID saved successfully
                    transactToMainActivity();
                } else {
                    // Handle the error
                    Toast.makeText(LoginActivity.this, "Failed to save gym ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // User is now signed out
                        signIn();
                    }
                });
    }
}
