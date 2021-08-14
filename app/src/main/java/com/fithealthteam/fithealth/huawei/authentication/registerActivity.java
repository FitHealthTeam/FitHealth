package com.fithealthteam.fithealth.huawei.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fithealthteam.fithealth.huawei.MainActivity;
import com.fithealthteam.fithealth.huawei.R;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.agconnect.auth.EmailUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.util.Locale;

public class registerActivity extends AppCompatActivity {

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
        RadioButton male = findViewById(R.id.male);
        RadioButton female = findViewById(R.id.female);
        Button submit = findViewById(R.id.submit_register);
        TextView login = findViewById(R.id.loginBtn);

        //authentication
        VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                .sendInterval(30)
                .locale(Locale.CHINA)
                .build();
        Button verifyrqt = findViewById(R.id.verify_request);
        EditText verifyCode = findViewById(R.id.verify_code);
        verifyrqt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!registerEmail.getText().toString().trim().equals(null) || !registerEmail.getText().toString().trim().isEmpty()) {
                    Task<VerifyCodeResult> task = AGConnectAuth.getInstance().requestVerifyCode(String.valueOf(registerEmail.getText()), settings);
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
                    Toast.makeText(getBaseContext(), "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //submit update value
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyCode.getText().toString().trim().equals(null) || verifyCode.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please insert the authentication code!", Toast.LENGTH_SHORT).show();
                }

                if(password.getText().toString().equals(null) || password.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please insert a password!", Toast.LENGTH_SHORT).show();
                }

                if(fname.getText().toString().equals(null) || fname.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(), "Please insert your first name!", Toast.LENGTH_SHORT).show();
                }

                if(lname.getText().toString().equals(null) || lname.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(), "Please insert your last name!", Toast.LENGTH_SHORT).show();
                }

                if(male.isSelected() && female.isSelected()){
                    Toast.makeText(getBaseContext(), "Please insert your gender!", Toast.LENGTH_SHORT).show();
                }

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
                                AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

                                //remember to update name gender

                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getBaseContext(), "Authentication code is not valid.", Toast.LENGTH_SHORT).show();
                            }
                        });
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
}