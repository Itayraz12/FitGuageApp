package com.example.fitgaugeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

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

            // After signing in, store the gymId
            if (user != null) {
                String userId = user.getUid();
                String gymId = "yourGymIdHere"; // Replace with the actual gym ID you want to associate with the user

                // Get a reference to the Firebase database
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                // Store the gymId under this user's profile
                userRef.child("gymId").setValue(gymId)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successfully stored gymId
                                Toast.makeText(LoginActivity.this, "Gym ID saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Failed to store gymId
                                Toast.makeText(LoginActivity.this, "Failed to save Gym ID. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            // Now move to the MainActivity
            transactToMainActivity();
        } else {
            // Handle sign-in failure
        }
    }


    private void saveUserGymAssociation(String userId, String gymId) {
        // Save the gym ID in the user's profile
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(userId);
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
