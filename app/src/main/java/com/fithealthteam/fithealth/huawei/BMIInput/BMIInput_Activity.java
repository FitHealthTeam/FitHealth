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
import com.fithealthteam.fithealth.huawei.CloudDB.exercise;
import com.fithealthteam.fithealth.huawei.CloudDB.user;
import com.fithealthteam.fithealth.huawei.MainActivity;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.authentication.authenticateActivity;
import com.fithealthteam.fithealth.huawei.ui.settings.SettingsFragment;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;

import java.util.List;

public class BMIInput_Activity extends AppCompatActivity implements CloudDBZoneWrapper.userUICallBack {

    Button btnSave;
    EditText inputWeight,inputHeight;

    double weight;
    double height;

    private Handler handler = new Handler();
    private CloudDBZoneWrapper cloudDBZoneWrapperInstance;

    AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

    public BMIInput_Activity(){ cloudDBZoneWrapperInstance = new CloudDBZoneWrapper();}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmiinput);

        btnSave = findViewById(R.id.btnSave);
        inputWeight = findViewById(R.id.weight);
        inputHeight = findViewById(R.id.height);

        //proceed to initialize cloudDBZoneWrapper
        handler.post(()->{
            initCloudDBZone();
        });

        //execute the cloudDB task, delayed a bit to wait for initialization complete
        handler.post(()->{
            queryAll();
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get Text From User Input
                if(inputWeight.getText().length() == 0 || inputHeight.length() == 0){
                    Toast.makeText(getApplicationContext(), "Both Weight and Height are Required!", Toast.LENGTH_SHORT).show();
                }else{
                    weight = Double.parseDouble(String.valueOf(inputWeight.getText()));
                    height = Double.parseDouble(String.valueOf(inputHeight.getText()))/100;

                    handler.post(()->{
                        cloudDBZoneWrapperInstance.updateWeight(user.getUid(),weight,height);
                    });

                    //Toast.makeText(getApplicationContext(), "Weight: " + weight + "; Height: " + height , Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Your Details Has Been Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //close the CloudDB Zone Properly
        cloudDBZoneWrapperInstance.closeCloudDBZone();
    }

    //initialize cloudDBZone
    private void initCloudDBZone(){
        handler.post(()->{
            //add callback into cloudDBZoneWrapper
            cloudDBZoneWrapperInstance.addUserCallBack(BMIInput_Activity.this);

            //initialize
            cloudDBZoneWrapperInstance.createObjectType();
            cloudDBZoneWrapperInstance.openCloudDBZone();
        });
    }

    public void queryAll(){
        handler.postDelayed(()->{
            CloudDBZoneQuery<user> query2 = CloudDBZoneQuery.where(user.class).equalTo("id",user.getUid());
            cloudDBZoneWrapperInstance.queryUser(query2);
        },500);
    }


    @Override
    public void userOnAddorQuery(List<com.fithealthteam.fithealth.huawei.CloudDB.user> userList) {

    }

    @Override
    public void userOnSubscribe(List<com.fithealthteam.fithealth.huawei.CloudDB.user> userList) {

    }

    @Override
    public void userOnDelete(List<com.fithealthteam.fithealth.huawei.CloudDB.user> userList) {

    }

    @Override
    public void userShowError(String error) {

    }
}