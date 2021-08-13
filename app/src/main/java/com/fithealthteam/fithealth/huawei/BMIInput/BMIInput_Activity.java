package com.fithealthteam.fithealth.huawei.BMIInput;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fithealthteam.fithealth.huawei.MainActivity;
import com.fithealthteam.fithealth.huawei.R;
import com.fithealthteam.fithealth.huawei.ui.home.HomeFragment;

public class BMIInput_Activity extends AppCompatActivity {

    Button btnSave;
    EditText inputWeight,inputHeight;

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
                //calculateBMI();

                float weight = Float.parseFloat(String.valueOf(inputWeight.getText()));
                float height = Float.parseFloat(String.valueOf(inputHeight.getText()))/100;

                float bmi = weight / (height * height);

                String bmiResult = String.format("%.2f",bmi);

                Bundle bundle = new Bundle();
                bundle.putString("BMI",bmiResult);
                HomeFragment home = new HomeFragment();
                home.setArguments(bundle);


                finish();

            }
        });


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