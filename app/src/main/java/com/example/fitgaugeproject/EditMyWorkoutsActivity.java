    package com.example.fitgaugeproject;

    import android.os.Bundle;

    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.example.fitgaugeproject.Data.DataManager;
    import com.example.fitgaugeproject.Models.week;
    import com.google.android.material.button.MaterialButton;
    import com.google.android.material.textfield.TextInputEditText;
    import com.google.android.material.textview.MaterialTextView;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    public class EditMyWorkoutsActivity extends AppCompatActivity {

        private MaterialTextView main_LBL_title;
        private MaterialButton main_BTN_save;
        private TextInputEditText main_ET_text;
        private MaterialButton main_BTN_updateTitle;
        private MaterialButton main_BTN_load;
        private MaterialTextView main_LBL_data;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_my_workouts);

            findViews();
            initViews();
            updateTitleFromDB();
        }

        private void updateTitleFromDB() {
            DatabaseReference titleRef = FirebaseDatabase.getInstance().getReference("title");

    //        titleRef.addListenerForSingleValueEvent( // For one time data fetching from DB.
            titleRef.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String value = snapshot.getValue(String.class);
                            main_LBL_title.setText(value);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //pass
                        }
                    });
        }

        private void initViews() {
           main_BTN_save.setOnClickListener(v -> saveDataToFirebase());
            //main_BTN_save.setOnClickListener(v -> updateCar("269-69-402"));
            //main_BTN_updateTitle.setOnClickListener(v -> setLabel(main_ET_text.getText().toString()));
            main_BTN_load.setOnClickListener(v -> loadDataAndShowOnScreen());

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

                }
            });
        }

    //    private void updateWorkout(String lp) {
    //        FirebaseDatabase database = FirebaseDatabase.getInstance();
    //        DatabaseReference weekRef = database.getReference("weekOfWorkouts");
    //
    //        weekRef
    //                .child("allCars")
    //                .child(lp)
    //                .child("price")
    //                .setValue(74_000L);
    //    }

        private void setLabel(String string) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference titleRef = database.getReference("title");

            titleRef.setValue(string);
        }

        private void saveDataToFirebase() {
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference garageRef = database.getReference("Week of products");

            garageRef.setValue(DataManager.createWeekOfWorkouts());
        }

        private void findViews() {
            main_LBL_title = findViewById(R.id.main_LBL_title);  // Corrected ID reference
            main_BTN_save = findViewById(R.id.main_BTN_save);
            main_ET_text = findViewById(R.id.main_ET_text);
            main_BTN_updateTitle = findViewById(R.id.main_BTN_updateTitle);
            main_BTN_load = findViewById(R.id.main_BTN_load);
            main_LBL_data = findViewById(R.id.main_LBL_data);
        }
    }