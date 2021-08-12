package com.fithealthteam.fithealth.huawei.myplan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.dialogbox.AddMealDialogActivity;

public class MyPlanActivity extends AppCompatActivity implements AddMealDialogActivity.AddMealDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plan);
        //hide the top action bar and title
        getSupportActionBar().hide();

        Button addMealBtn = findViewById(R.id.addMealBtn);

        addMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMealDialogActivity newMealDialog = new AddMealDialogActivity();
                newMealDialog.show(getSupportFragmentManager(), "Add New Meal Dialog");
            }
        });
    }

    @Override
    public void applynewMeal(String newMealName, String newMealCalories) {
        //process the data from add new meal dialog here
        if(newMealName.trim().isEmpty() || newMealCalories.trim().isEmpty()){
            //warn user about the empty data
            Toast.makeText(getApplicationContext(),"You should not enter the empty meal name or calories !", Toast.LENGTH_SHORT).show();
        }else {
            //continue process data
        }
    }
}