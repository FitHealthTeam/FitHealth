package com.fithealthteam.fithealth.huawei.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fithealthteam.fithealth.huawei.R;

import java.util.Random;

public class backgroundProcess3 extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MyAPP3", "background is called");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, intent.getStringExtra("channelID"))
                    .setSmallIcon(R.drawable.auth_fithealthlogo)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setContentTitle(intent.getStringExtra("pushTitle"))
                    .setContentText(intent.getStringExtra("pushMessage"))
                    .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setAutoCancel(true);

            int notifyID = new Random().nextInt(9999);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notifyID,builder.build());
        }
}
