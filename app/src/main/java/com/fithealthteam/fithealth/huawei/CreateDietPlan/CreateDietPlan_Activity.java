package com.fithealthteam.fithealth.huawei.CreateDietPlan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fithealthteam.fithealth.huawei.BMIInput.BMIInput_Activity;
import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.MainActivity;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.authentication.authenticateActivity;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;

public class CreateDietPlan_Activity extends AppCompatActivity {

    TextView tvNext;

    private CloudDBZoneWrapper cloudDBZoneWrapperInstance;

    public CreateDietPlan_Activity(){ cloudDBZoneWrapperInstance = new CloudDBZoneWrapper();}

    AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diet_plan);

        CloudDBZoneWrapper.initAGConnectCloudDB(getApplicationContext());

        tvNext = findViewById(R.id.tvNext);

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                if(user == null){
                    intent = new Intent(getApplicationContext(), authenticateActivity.class);

                }else{
                    intent = new Intent(getApplicationContext(), BMIInput_Activity.class);
                }
                startActivity(intent);
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

}
