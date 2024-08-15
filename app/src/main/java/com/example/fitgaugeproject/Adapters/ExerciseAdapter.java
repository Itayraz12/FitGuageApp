package com.example.fitgaugeproject.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.example.fitgaugeproject.Models.exercise;
import com.example.fitgaugeproject.R;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final ArrayList<exercise> exercises;

    public ExerciseAdapter(ArrayList<exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        exercise exerciseItem = exercises.get(position);
        holder.exerciseName.setText(exerciseItem.getExerciseName());
        holder.exerciseSets.setText("Sets: " + exerciseItem.getNumberOfSets());
        holder.exerciseReps.setText("Reps: " + exerciseItem.getNumberOfRepetitions());

        // Load the Lottie animation based on the animationResId from the database
        String animationResId = exerciseItem.getAnimationResId();
        Log.d("ExerciseAdapter", "Loading animation: " + animationResId);
        if (animationResId != null && !animationResId.isEmpty()) {
            // Remove the .json extension if present
            if (animationResId.endsWith(".json")) {
                animationResId = animationResId.substring(0, animationResId.length() - 5);
            }

            // Get the resource ID
            int resId = holder.itemView.getContext().getResources().getIdentifier(animationResId, "raw", holder.itemView.getContext().getPackageName());

            if (resId != 0) { // Check if the resource was found
                holder.exerciseAnimation.setAnimation(resId);
            } else {
                holder.exerciseAnimation.setVisibility(View.GONE); // Hide if no animation is found
            }
        } else {
            holder.exerciseAnimation.setVisibility(View.GONE); // Hide if no animation is found
        }




        // Handle YouTube link click
        holder.youtubeLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(exerciseItem.getYoutubeUrl()));
            holder.itemView.getContext().startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView exerciseName;
        private final MaterialTextView exerciseSets;
        private final MaterialTextView exerciseReps;
        private final LottieAnimationView exerciseAnimation;
        private final Button youtubeLink;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            exerciseSets = itemView.findViewById(R.id.exercise_sets);
            exerciseReps = itemView.findViewById(R.id.exercise_reps);
            exerciseAnimation = itemView.findViewById(R.id.exercise_animation);
            youtubeLink = itemView.findViewById(R.id.youtube_link);
        }
    }
}


