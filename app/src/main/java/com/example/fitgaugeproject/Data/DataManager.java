package com.example.fitgaugeproject.Data;

import com.example.fitgaugeproject.Models.exercise;
import com.example.fitgaugeproject.Models.week;
import com.example.fitgaugeproject.Models.workout;

public class DataManager {

    public static week createWeekOfWorkouts() {
        week weekOfWorkouts = new week();
        weekOfWorkouts.getAllWorkouts()
                .put("First workout",
                        new workout()
                                .setIsDone(false)
                                .setType(workout.WorkOutType.STRENGTH_TRAINING)
                                .addExercise(new exercise("banch presss",3,12)));



        return weekOfWorkouts;
    }
}
