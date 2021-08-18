package com.fithealthteam.fithealth.huawei.BMIInput;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.user;
import com.fithealthteam.fithealth.huawei.MainActivity;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.authentication.authenticateActivity;
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

        btnSave = findViewById(R.id.btnSave);
        inputWeight = findViewById(R.id.weight);
        inputHeight = findViewById(R.id.height);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double weight = Float.parseFloat(String.valueOf(inputWeight.getText()));
                double height = Float.parseFloat(String.valueOf(inputHeight.getText()))/100;

                addWeightHeight(newUser,height,weight);
                newUser.setId(user.getUid());

                cloudDBZoneWrapperInstance.upsertUser(newUser);

                Toast.makeText(getApplicationContext(), "Weight: " + weight + "; Height: " + height , Toast.LENGTH_SHORT).show();


                if(user == null){
                    Intent intent = new Intent(getApplicationContext(), authenticateActivity.class);
                    startActivity(intent);
                }else{
                    Log.d("HMS Auth User", user.getEmail());
                    Log.d("HMS Auth User UID", user.getUid());
                }
                finish();
            }
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

    public void addWeightHeight(user user,double mHeight,double mWeight){
        user.setHeight(mHeight);
        user.setWeight(mWeight);
    }


}