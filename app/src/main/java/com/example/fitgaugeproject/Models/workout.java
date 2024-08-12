package com.example.fitgaugeproject.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class workout implements Serializable {

    public enum WorkOutType {
        TRX,
        FUNCTIONAL_TRAINING,
        STRENGTH_TRAINING,
        HYBRID,
        AEROBIC_TRAINING
    }

    private String workoutName;
    private String description;
    private String imageUrl;
    private ArrayList<exercise> exercises;
    private WorkOutType type = WorkOutType.AEROBIC_TRAINING;
    private Boolean isDone = Boolean.FALSE;

    public workout() {
        exercises = new ArrayList<>();
    }

    public workout(String workoutName, WorkOutType type) {
        this.workoutName = workoutName;
        this.type = type;
        this.exercises = new ArrayList<>();
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getDescription() {
        return description;
    }

    public workout setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public workout setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public workout setIsDone(Boolean done) {
        isDone = done;
        return this;
    }

    public ArrayList<exercise> getExercises() {
        return exercises;
    }

    public workout addExercise(exercise exercise) {
        exercises.add(exercise);
        return this;
    }

    public workout removeExercise(exercise exercise) {
        exercises.remove(exercise);
        return this;
    }

    public WorkOutType getType() {
        return type;
    }

    public workout setType(WorkOutType type) {
        this.type = type;
        return this;
    }

    public workout addExercise(String exerciseName, int numberOfSets, int numberOfRepetitions) {
        exercise newExercise = new exercise(exerciseName, numberOfSets, numberOfRepetitions);
        exercises.add(newExercise);
        return this;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "workoutName='" + workoutName + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", type=" + type +
                ", isDone=" + isDone +
                ", exercises=" + exercises +
                '}';
    }
}
