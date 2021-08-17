package com.fithealthteam.fithealth.huawei.Notification;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.HmsMessaging;

public class PushNotificationService extends HmsMessageService {
    private static final String TAG = "PushKit";

    Context context;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i(TAG, "Receive token : " + s);
    }

    @Override
    public void onNewToken(String s, Bundle bundle) {
        super.onNewToken(s, bundle);
        Log.i(TAG, "Receive token : " + s);
    }
/*
    //subscribe the notification
    public void subscribe(String topic) {
        try {
            // Subscribe to a topic.
            HmsMessaging.getInstance(getBaseContext()).subscribe(topic)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            // Obtain the topic subscription result.
                            if (task.isSuccessful()) {
                                Log.i(TAG, "subscribe topic successfully");
                            } else {
                                Log.e(TAG, "subscribe topic failed, return value is " + task.getException().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "subscribe failed, catch exception : " + e.getMessage());
        }
    }

    //unsubscribe the notification
    public void unsubscribe(String topic) {
        try {
            // Unsubscribe from a topic.
            HmsMessaging.getInstance(getBaseContext()).unsubscribe(topic)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            // Obtain the topic unsubscription result.
                            if (task.isSuccessful()) {
                                Log.i(TAG, "unsubscribe topic successfully");
                            } else {
                                Log.e(TAG, "unsubscribe topic failed, return value is " + task.getException().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "unsubscribe failed, catch exception : " + e.getMessage());
        }
    }*/
}
