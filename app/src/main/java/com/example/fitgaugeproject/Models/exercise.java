package com.example.fitgaugeproject.Models;

public class exercise {

    private String exerciseName = "";
    private int numberOfSets ;
    private int numberOfRepetitions ;




    public exercise(){

    }

    public exercise(String exerciseName, int numberOfSets, int numberOfRepetitions) {
        this.exerciseName = exerciseName;
        this.numberOfSets = numberOfSets;
        this.numberOfRepetitions = numberOfRepetitions;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getNumberOfSets() {
        return numberOfSets;
    }

    public void setNumberOfSets(int numberOfSets) {
        this.numberOfSets = numberOfSets;
    }

    public int getNumberOfRepetitions() {
        return numberOfRepetitions;
    }

    public void setNumberOfRepetitions(int numberOfRepetitions) {
        this.numberOfRepetitions = numberOfRepetitions;
    }

    @Override
    public String toString() {
        return "exercise{" +
                "exerciseName='" + exerciseName + '\'' +
                ", numberOfSets=" + numberOfSets +
                ", numberOfRepetitions=" + numberOfRepetitions +
                '}';
    }
}
