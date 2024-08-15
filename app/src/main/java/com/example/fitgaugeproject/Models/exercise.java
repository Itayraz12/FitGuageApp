package com.example.fitgaugeproject.Models;

public class exercise {

    private String exerciseName;
    private int numberOfSets;
    private int numberOfRepetitions;
    private String youtubeUrl;
    private String animationResId;


    public exercise(){

    }

    public exercise(String exerciseName, int numberOfSets, int numberOfRepetitions) {
        this.exerciseName = exerciseName;
        this.numberOfSets = numberOfSets;
        this.numberOfRepetitions = numberOfRepetitions;
    }


    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getAnimationResId() {
        return animationResId;
    }

    public void setAnimationResId(String animationResId) {
        this.animationResId = animationResId;
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
