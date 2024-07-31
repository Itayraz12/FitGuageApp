package com.example.fitgaugeproject.Models;

import java.util.ArrayList;
import java.util.List;

public class workout {

        public enum WorkOutType{
            FUNCTIONAL_TRAINING,
            STRENGTH_TRAINING,
            HYBRID,
            AEROBIC_TRAINING
        }

    private ArrayList<exercise> exercises;
    private WorkOutType type = WorkOutType.AEROBIC_TRAINING;


    private Boolean isDone = Boolean.FALSE;


    public workout() {
        exercises = new ArrayList<>();
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
        exercise newExercise = new exercise();
        newExercise.setExerciseName(exerciseName);
        newExercise.setNumberOfSets(numberOfSets);
        newExercise.setNumberOfRepetitions(numberOfRepetitions);
        exercises.add(newExercise);
        return this;
    }

    @Override
    public String toString() {
        return "workout{" +
                "isDone=" + isDone +
                ", exercises=" + exercises +
                '}';
    }
}

