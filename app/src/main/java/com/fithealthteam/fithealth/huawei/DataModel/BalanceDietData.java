package com.fithealthteam.fithealth.huawei.DataModel;

public class BalanceDietData {
    private String mealName;
    private int calories;
    private boolean completeStatus;

    public BalanceDietData(String mealName, int calories, boolean completeStatus) {
        this.mealName = mealName;
        this.calories = calories;
        this.completeStatus = completeStatus;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public boolean isCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(boolean completeStatus) {
        this.completeStatus = completeStatus;
    }
}
