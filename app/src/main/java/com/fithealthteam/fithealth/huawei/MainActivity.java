package com.fithealthteam.fithealth.huawei;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;

import com.fithealthteam.fithealth.huawei.CloudDB.CloudDBZoneWrapper;
import com.fithealthteam.fithealth.huawei.CreateDietPlan.CreateDietPlan_Activity;
import com.fithealthteam.fithealth.huawei.Notification.backgroundProcess;
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

            // Intent to CreateDiet
            /*Intent intent = new Intent(getApplicationContext(),CreateDietPlan_Activity.class);
            startActivity(intent);*/

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
        Handler handler = new Handler();

        SharedPreferences pref = getSharedPreferences("MySharedPreferences",0);

       boolean excesssCalorySwitch = pref.getBoolean("excessCalories",  false);
       boolean drinkWaterReminderSwitch = pref.getBoolean("drinkWaterReminder",  false);
       boolean subscriptionSwitch = pref.getBoolean("subscription", false);

        if(excesssCalorySwitch == true) {
            Log.i("excessNotify", "excessCalories is reminding");
            initializeNotification("fithealth1");
            notifyMessage(this,8*60*60, "Calories Intake Reminder"
                    , "Reminder: Beware with your calories intake per day!");
        }

        if(drinkWaterReminderSwitch == true) {
            Log.i("drinkNotify", "drinkWater is reminding");
            initializeNotification("fithealth2");
            notifyMessage(this,(24/8)*60*60, "Drink Water Notification"
                    , "Reminder: Remember to drink your water!");
        }

        if(subscriptionSwitch == true) {
            Log.i("subscribeNotify", "subscription is reminding");
            initializeNotification("fithealth3");
            notifyMessage(this,12*60*60, "Subcription Reminder"
                    , "Don' missed out our new tips!");
        }
    }

    public void notifyMessage(Context c, double interval, String pushTitle, String pushMessage) {
        Intent intent = new Intent(c, backgroundProcess.class);
        intent.putExtra("pushTitle", pushTitle);
        intent.putExtra("pushMessage", pushMessage);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 1000, (long) (1000*interval), pendingIntent);
    }

    public void initializeNotification(String channel_id){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "FitHealthReminderChannel";
            String description = "Channel for fithealth";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
