package com.fithealthteam.fithealth.huawei.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fithealthteam.fithealth.huawei.MainActivity;
import com.fithealthteam.fithealth.huawei.R;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.util.Locale;

public class passResetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_reset);

        EditText email = findViewById(R.id.email_chgpass);
        Button obtainVerifycode = findViewById(R.id.obt_verify);

        obtainVerifycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().isEmpty() || !email.getText().toString().trim().equals(null)) {
                    VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                            .action(VerifyCodeSettings.ACTION_RESET_PASSWORD)
                            .sendInterval(30)
                            .locale(Locale.CHINA)
                            .build();
                    Task<VerifyCodeResult> task = AGConnectAuth.getInstance().requestVerifyCode(email.getText().toString().trim(), settings);
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

        Button submit = findViewById(R.id.chgpass_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newpass = findViewById(R.id.chg_password);
                EditText verifycode = findViewById(R.id.verify_chg);

                if(email.getText().toString().equals(null) || email.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter an email address!", Toast.LENGTH_SHORT).show();
                }

                if(verifycode.getText().toString().equals(null) || verifycode.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please insert your verification code!", Toast.LENGTH_SHORT).show();
                }

                if(newpass.getText().toString().equals(null) || newpass.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(), "Please insert your new password!", Toast.LENGTH_SHORT).show();
                }

                AGConnectAuth.getInstance().resetPassword(email.getText().toString().trim(), newpass.getText().toString(), verifycode.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getBaseContext(), "Password have been successfully change!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getBaseContext(), "Fail to change password", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}