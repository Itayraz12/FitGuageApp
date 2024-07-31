package com.example.fitgaugeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout;
    private Button  btnEditPlan;
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
                // Navigate to Training Plan Activity
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
}
