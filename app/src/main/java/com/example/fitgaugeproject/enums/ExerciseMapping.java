package com.example.fitgaugeproject.enums;

public enum ExerciseMapping {
    LEG_PRESS("leg press", "https://www.youtube.com/watch?v=B6rGDcfyPto", "leg_press_animation"),
    BENCH_PRESS("bench press", "https://www.youtube.com/watch?v=gRVjAtPip0Y", "bench_press_animation"),

    SQUAT("squat", "https://www.youtube.com/watch?v=ubdIGnX2Hfs&t=250s","squat_animation"),

    SNATCH ("snatch","https://www.youtube.com/watch?v=UBc5N_-xdqo", "power_lift_animation"),

    DEADLIFT ("dead lift", "https://www.youtube.com/watch?v=r4MzxtBKyNE", "dead_lift_animation");

    private final String exerciseName;
    private final String youtubeUrl;
    private final String animationResId;

    ExerciseMapping(String exerciseName, String youtubeUrl, String animationResId) {
        this.exerciseName = exerciseName;
        this.youtubeUrl = youtubeUrl;
        this.animationResId = animationResId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public String getAnimationResId() {
        return animationResId;
    }

    // Method to get ExerciseMapping by exercise name
    public static ExerciseMapping getByExerciseName(String name) {
        for (ExerciseMapping mapping : values()) {
            if (mapping.getExerciseName().equalsIgnoreCase(name)) {
                return mapping;
            }
        }
        return null; // Return null if no matching exercise name is found
    }
    }
