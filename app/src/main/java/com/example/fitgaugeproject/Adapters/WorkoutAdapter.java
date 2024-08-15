package com.example.fitgaugeproject.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitgaugeproject.Models.workout;
import com.example.fitgaugeproject.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private final ArrayList<workout> workouts;

    public WorkoutAdapter(ArrayList<workout> workouts) {
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        workout workoutItem = workouts.get(position);
        holder.workoutName.setText(workoutItem.getWorkoutName());
        holder.workoutType.setText(workoutItem.getType().toString().replace("_", " "));
        holder.workoutStatus.setText(workoutItem.getIsDone() ? "Completed" : "Pending");

        // Load image using Glide
        if (workoutItem.getImageUrl() != null && !workoutItem.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(workoutItem.getImageUrl())
                    .placeholder(R.drawable.image_placeholder) // Ensure this drawable exists
                    .into(holder.workoutImage);
        } else {
            // Set image based on workout type if no image URL is provided
            switch (workoutItem.getType()) {
                case AEROBIC_TRAINING:
                    holder.workoutImage.setImageResource(R.drawable.aerobic_training);
                    break;
                case HYBRID:
                    holder.workoutImage.setImageResource(R.drawable.hybrid_training);
                    break;
                case STRENGTH_TRAINING:
                    holder.workoutImage.setImageResource(R.drawable.strength_training);
                    break;
                case FUNCTIONAL_TRAINING:
                    holder.workoutImage.setImageResource(R.drawable.functional_training);
                    break;
                case TRX:
                    holder.workoutImage.setImageResource(R.drawable.trx);
                    break;
                default:
                    holder.workoutImage.setImageResource(R.drawable.image_placeholder);
                    break;
            }
        }

        // Set up nested RecyclerView for exercises
        ExerciseAdapter exerciseAdapter = new ExerciseAdapter(workoutItem.getExercises());
        holder.exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.exercisesRecyclerView.setAdapter(exerciseAdapter);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView workoutName;
        private final MaterialTextView workoutType;
        private final MaterialTextView workoutStatus;
        private final ImageView workoutImage; // Add ImageView reference
        private final RecyclerView exercisesRecyclerView;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workout_name);
            workoutType = itemView.findViewById(R.id.workout_type);
            workoutStatus = itemView.findViewById(R.id.workout_status);
            workoutImage = itemView.findViewById(R.id.workout_image); // Initialize ImageView
            exercisesRecyclerView = itemView.findViewById(R.id.exercises_recycler_view);
        }
    }
}
