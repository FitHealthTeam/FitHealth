package com.fithealthteam.fithealth.huawei.myplan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fithealthteam.fithealth.huawei.R;

public class MyPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plan);
        //hide the top action bar and title
        getSupportActionBar().hide();
    }
}