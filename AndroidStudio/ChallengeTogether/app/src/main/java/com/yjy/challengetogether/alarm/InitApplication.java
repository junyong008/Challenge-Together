package com.yjy.challengetogether.alarm;

import android.app.Application;
import android.content.Context;

public class InitApplication extends Application {
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createNotificationChannel(getApplicationContext());
    }
}
