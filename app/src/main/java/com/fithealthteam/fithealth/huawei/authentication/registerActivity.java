package com.fithealthteam.fithealth.huawei.authentication;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CloudDB.user;
import com.fithealthteam.fithealth.huawei.MainActivity;
import com.fithealthteam.fithealth.huawei.R;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.EmailUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class registerActivity extends AppCompatActivity implements CloudDBZoneWrapper.userUICallBack {

    private Handler handler = new Handler();
    private CloudDBZoneWrapper cloudDBZoneWrapper;
    AGConnectUser user;


    public registerActivity() {
        cloudDBZoneWrapper = new CloudDBZoneWrapper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);
        getSupportActionBar().hide();

        EditText registerEmail = findViewById(R.id.email_register);
        EditText password = findViewById(R.id.password_register);
        EditText fname = findViewById(R.id.fname_register);
        EditText lname = findViewById(R.id.lname_register);
        RadioGroup rgGender = findViewById(R.id.gender);
        RadioButton male = findViewById(R.id.male);
        RadioButton female = findViewById(R.id.female);
        EditText dob = findViewById(R.id.dob);
        Button submit = findViewById(R.id.submit_register);
        TextView login = findViewById(R.id.loginBtn);

        //authentication
        VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                .sendInterval(30)
                .locale(Locale.SIMPLIFIED_CHINESE)
                .build();
        Button verifyrqt = findViewById(R.id.verify_request);
        EditText verifyCode = findViewById(R.id.verify_code);
        verifyrqt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!registerEmail.getText().toString().trim().equals(null) || !registerEmail.getText().toString().trim().isEmpty()) {
                    Task<VerifyCodeResult> task = AGConnectAuth.getInstance().requestVerifyCode(registerEmail.getText().toString().trim(), settings);
                    task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
                        @Override
                        public void onSuccess(VerifyCodeResult verifyCodeResult) {
                            Toast.makeText(getBaseContext(), "Verify code has been sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getBaseContext(), "Send verify code failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), "Please enter an email address!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //submit update value
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check = 0;

                if(verifyCode.getText().toString().trim().equals(null) || verifyCode.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please insert the authentication code!", Toast.LENGTH_SHORT).show();
                    check++;
                }

                if (registerEmail.getText().toString().trim().equals(null) || registerEmail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter an email address!", Toast.LENGTH_SHORT).show();
                    check++;
                }

                if(password.getText().toString().equals(null) || password.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please insert a password!", Toast.LENGTH_SHORT).show();
                    check++;
                }

                if(fname.getText().toString().equals(null) || fname.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(), "Please insert your first name!", Toast.LENGTH_SHORT).show();
                    check++;
                }

                if(lname.getText().toString().equals(null) || lname.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(), "Please insert your last name!", Toast.LENGTH_SHORT).show();
                    check++;
                }


                if(!male.isChecked() && !female.isChecked()){
                    Toast.makeText(getBaseContext(), "Please insert your gender!", Toast.LENGTH_SHORT).show();
                    check++;
                }

                if (check == 5) {
                    Toast.makeText(getBaseContext(), "success", Toast.LENGTH_SHORT).show();
                    check=0;
                    EmailUser emailUser = new EmailUser.Builder()
                            .setEmail(registerEmail.getText().toString().trim())
                            .setVerifyCode(verifyCode.getText().toString().trim())
                            .setPassword(password.getText().toString()) //optional
                            .build();
                    AGConnectAuth.getInstance().createUser(emailUser)
                            .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                                @Override
                                public void onSuccess(SignInResult signInResult) {
                                    // After an account is created, the user has signed in by default.
                                    user = AGConnectAuth.getInstance().getCurrentUser();
                                    user newUser = new user();
                                    newUser.setId(user.getUid());
                                    newUser.setFirstName(fname.getText().toString());
                                    newUser.setLastName(lname.getText().toString());
                                    newUser.setSubscribeTips(false);
                                    newUser.setDrinkWater(false);
                                    newUser.setHeight(0.00);
                                    newUser.setWeight(0.00);

                                    if(male.isChecked()) {
                                        newUser.setGender("Male");
                                    }

                                    if(female.isChecked()) {
                                        newUser.setGender("Female");
                                    }

                                    Date dobDate = null;
                                    try {
                                        dobDate = new SimpleDateFormat("yyyy-MM-dd").parse(dob.getText().toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    newUser.setDob(dobDate);
                                    addUserInfo(newUser);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getBaseContext(), "Error: " + e.getCause() , Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        //intent to login side if got account
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), loginActivity.class);
                startActivity(intent);
            }
        });
    }


    //Initialize Cloud DB Wrapper to use
    public void initCloudDBWrapper(){
        handler.post(() -> {
            cloudDBZoneWrapper.addUserCallBack(registerActivity.this);
            cloudDBZoneWrapper.createObjectType();
            cloudDBZoneWrapper.openCloudDBZone();
        });
    }

    public void addUserInfo (user user) {

        handler.post(()->{
            initCloudDBWrapper();
        });

        handler.postDelayed(()->{
            cloudDBZoneWrapper.upsertUser(user);
        }, 500);

        handler.postDelayed(()->{
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        },1000);
    }

    protected void onDestroy() {
        handler.post(cloudDBZoneWrapper::closeCloudDBZone);
        super.onDestroy();
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