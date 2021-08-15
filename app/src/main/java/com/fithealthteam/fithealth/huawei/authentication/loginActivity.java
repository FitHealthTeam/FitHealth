package com.fithealthteam.fithealth.huawei.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.MainActivity;
import com.fithealthteam.fithealth.huawei.R;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;

public class loginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);
        getSupportActionBar().hide();

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if(user == null) {
            EditText emailLogin = findViewById(R.id.email_login);
            EditText passLogin = findViewById(R.id.password_login);
            Button loginBtn = findViewById(R.id.submit_login);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AGConnectAuthCredential credential = EmailAuthProvider.credentialWithPassword(emailLogin.getText().toString().trim(), passLogin.getText().toString());
                    AGConnectAuth.getInstance().signIn(credential)
                            .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                                @Override
                                public void onSuccess(SignInResult signInResult) {
                                    Toast.makeText(getBaseContext(), "Successfully Login!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getBaseContext(), "Invalid password or email address!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

        }

        TextView toregister = findViewById(R.id.register_tv);
        toregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), registerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView forgotpass = findViewById(R.id.forgot_password);
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), passResetActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}