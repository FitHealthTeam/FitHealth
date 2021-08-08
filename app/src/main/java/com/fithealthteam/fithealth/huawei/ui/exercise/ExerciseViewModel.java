package com.fithealthteam.fithealth.huawei.ui.exercise;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExerciseViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> taskCompletion;
    private MutableLiveData<String> percentageIndicator;

    public MutableLiveData<String> getTaskCompletion() {
        return taskCompletion;
    }

    public void setTaskCompletion(MutableLiveData<String> taskCompletion) {
        this.taskCompletion = taskCompletion;
    }

    public MutableLiveData<String> getPercentageIndicator() {
        return percentageIndicator;
    }

    public void setPercentageIndicator(MutableLiveData<String> percentageIndicator) {
        this.percentageIndicator = percentageIndicator;
    }
}