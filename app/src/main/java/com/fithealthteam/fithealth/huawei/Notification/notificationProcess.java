package com.fithealthteam.fithealth.huawei.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fithealthteam.fithealth.huawei.R;

public class notificationProcess extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "FitHealth")
                .setSmallIcon(R.drawable.auth_fithealthlogo)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setContentTitle("Drink Water Reminder")
                .setContentText("Hurry, is time to drink water ! Take a rest and drink a cup of water now.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200,builder.build());
    }
}
