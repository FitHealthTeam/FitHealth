package com.fithealthteam.fithealth.huawei.BMIInput;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.user;
import com.fithealthteam.fithealth.huawei.R;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;

public class BMIInput_Activity extends AppCompatActivity {

    Button btnSave;
    EditText inputWeight,inputHeight;

    private Handler handler;
    private CloudDBZoneWrapper cloudDBZoneWrapperInstance;

    public BMIInput_Activity(){ cloudDBZoneWrapperInstance = new CloudDBZoneWrapper();}

    user newUser = new user();

    AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmiinput);

        //hide the top action bar and title
        getSupportActionBar().hide();

        //prepare cloudDBZoneWrapper
        handler.post(() -> {
            initCloudDBWrapper();
        });

        btnSave = findViewById(R.id.btnSave);
        inputWeight = findViewById(R.id.weight);
        inputHeight = findViewById(R.id.height);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calculateBMI();

                double weight = Float.parseFloat(String.valueOf(inputWeight.getText()));
                double height = Float.parseFloat(String.valueOf(inputHeight.getText()))/100;

                addWeightHeight(newUser,height,weight);

                newUser.setId(user.getUid());

                cloudDBZoneWrapperInstance.upsertUser(newUser);


                //float bmi = weight / (height * height);

                //String bmiResult = String.format("%.2f",bmi);



                /*Bundle bundle = new Bundle();
                bundle.putString("BMI",bmiResult);
                HomeFragment home = new HomeFragment();
                home.setArguments(bundle);*/




                //finish();

            }
        });


    }

    public void addWeightHeight(user user,double mHeight,double mWeight){
        user.setHeight(mHeight);
        user.setWeight(mWeight);
    }

    //Initialize Cloud DB Wrapper to use
    public void initCloudDBWrapper(){
        handler.postDelayed(() -> {
            cloudDBZoneWrapperInstance.addCallBack((CloudDBZoneWrapper.exerciseUICallBack) BMIInput_Activity.this);
            cloudDBZoneWrapperInstance.createObjectType();
            cloudDBZoneWrapperInstance.openCloudDBZone();
            AGConnectUser userAG = AGConnectAuth.getInstance().getCurrentUser();
            CloudDBZoneQuery<user> query = CloudDBZoneQuery.where(user.class)
                    .equalTo("uid", userAG.getUid());
                    //.equalTo("deleteStatus", false);
            cloudDBZoneWrapperInstance.queryUser(query);

        }, 500);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //close the CloudDB Zone Properly
        cloudDBZoneWrapperInstance.closeCloudDBZone();
    }

    public void calculateBMI (){

        float weight = Float.parseFloat(String.valueOf(inputWeight.getText()));
        float height = (Float.parseFloat(String.valueOf(inputHeight.getText()))/100);

        float bmi = weight / (height * height);

        //String bmiResult = String.valueOf(bmi);

        TextView tvBMI = findViewById(R.id.tvBMIResult);
        tvBMI.setText(bmi + " kg/m2");

    }
}