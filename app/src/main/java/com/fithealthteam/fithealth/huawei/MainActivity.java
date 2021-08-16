package com.fithealthteam.fithealth.huawei;

import static com.fithealthteam.fithealth.huawei.Notification.notify.CHANNEL_ID_1;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.authentication.authenticateActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fithealthteam.fithealth.huawei.databinding.ActivityMainBinding;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    //private NotificationManagerCompat notificationManager;

    //account for testing and debuging
    //id = fh1@xkx.me
    //password = test123456

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //initialize the AGConnectCloudDB for CloudDB use
        CloudDBZoneWrapper.initAGConnectCloudDB(getApplicationContext());

        super.onCreate(savedInstanceState);

        //hide the top action bar and title
        getSupportActionBar().hide();

        // if (not login) --> another user activity --
       AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), authenticateActivity.class);
            startActivity(intent);
            finish();
        }else{
            Log.d("HMS Auth User", user.getEmail());
            Log.d("HMS Auth User UID", user.getUid());
        }

        //li-hao check for whether newUser is true --> if (newUser == true) --> go to create Diet plan activity to load data --> store to db --> intent back to here


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.exerciseFragment, R.id.communityFragment, R.id.settingsFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //notificationManager = NotificationManagerCompat.from(this);


    }
/*
    public void sendToNotifyChannel (View v){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.auth_fithealthlogo)
                .setContentTitle("Excessive Calories Intake")
                .setContentText("Warning: You have exceed your daily calories consuption!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);

    }*/
}
