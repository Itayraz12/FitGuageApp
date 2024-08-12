package com.example.fitgaugeproject.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView exerciseName;
        private final MaterialTextView exerciseSets;
        private final MaterialTextView exerciseReps;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            exerciseSets = itemView.findViewById(R.id.exercise_sets);
            exerciseReps = itemView.findViewById(R.id.exercise_reps);
        }
    }
}

