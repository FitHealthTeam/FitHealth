package com.fithealthteam.fithealth.huawei.CreateDietPlan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fithealthteam.fithealth.huawei.BMIInput.BMIInput_Activity;
import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.user;
import com.fithealthteam.fithealth.huawei.MainActivity;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.authentication.authenticateActivity;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;

public class CreateDietPlan_Activity extends AppCompatActivity {

    TextView tvNext;
    RadioGroup weightGoal;
    RadioGroup exercisePerWeek;
    RadioButton rbWeightGoal;
    RadioButton rbExercisePerWeek;
    ToggleButton btnYes;
    ToggleButton btnNo;
    Boolean vegetarian;

    private Handler handler;
    private CloudDBZoneWrapper cloudDBZoneWrapperInstance;

    public CreateDietPlan_Activity(){ cloudDBZoneWrapperInstance = new CloudDBZoneWrapper();}

    AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diet_plan);

        //CloudDBZoneWrapper.initAGConnectCloudDB(getApplicationContext());

        weightGoal = findViewById(R.id.loseWeightGoal);
        exercisePerWeek = findViewById(R.id.exercisePerWeek);

        btnYes = findViewById(R.id.btnYesTog);
        btnNo = findViewById(R.id.btnNoTog);

        tvNext = findViewById(R.id.tvNext);
        

        tvNext.setOnClickListener(v -> {

            //String temp = null;

            if(btnYes.isChecked()){
                vegetarian = true;
                //temp = "Yes";
            }else if(btnNo.isChecked()){
                vegetarian = false;
                //temp = "No";
            }

            returnRbSelection();

            /*Toast.makeText(getApplicationContext(), "Lose Weight Goal: " + rbWeightGoal.getText()
                    + "; Exercise Per Week: " + rbExercisePerWeek.getText()
                    + "; Vegetarian: " + temp, Toast.LENGTH_LONG).show();*/

            Intent intent;
            if(user == null){
                intent = new Intent(getApplicationContext(), authenticateActivity.class);
            }else{
                intent = new Intent(getApplicationContext(), BMIInput_Activity.class);
            }
            startActivity(intent);
            finish();
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //close the CloudDB Zone Properly
        cloudDBZoneWrapperInstance.closeCloudDBZone();
    }

    //Initialize Cloud DB Wrapper to use
    public void initCloudDBWrapper(){
        handler.postDelayed(() -> {
            cloudDBZoneWrapperInstance.addCallBack((CloudDBZoneWrapper.exerciseUICallBack) CreateDietPlan_Activity.this);
            cloudDBZoneWrapperInstance.createObjectType();
            cloudDBZoneWrapperInstance.openCloudDBZone();
            AGConnectUser userAG = AGConnectAuth.getInstance().getCurrentUser();
            CloudDBZoneQuery<com.fithealthteam.fithealth.huawei.CloudDB.user> query = CloudDBZoneQuery.where(user.class)
                    .equalTo("uid", userAG.getUid());
            //.equalTo("deleteStatus", false);
            cloudDBZoneWrapperInstance.queryUser(query);

        }, 500);
    }

    public void returnRbSelection(){

        int radioIdGoal = weightGoal.getCheckedRadioButtonId();
        rbWeightGoal = findViewById(radioIdGoal);

        int radioIdPerWeek = exercisePerWeek.getCheckedRadioButtonId();
        rbExercisePerWeek = findViewById(radioIdPerWeek);

    }

}
