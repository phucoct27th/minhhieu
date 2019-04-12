package com.project.hieu.ghichu.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Calendar;

/**
 * Created by Admin on 10/29/2016.
 */
public class AlarmService extends Service {
    PendingIntent pendingIntent;
    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intentTime = new Intent();
        intentTime.setAction("com.project.hieu.ghichu.THOI_GIAN_HIEN_TAI");
        sendBroadcast(intentTime);
        Calendar calendar = Calendar.getInstance();
        pendingIntent = PendingIntent.getBroadcast(AlarmService.this,0,intentTime,0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),60000,pendingIntent);
//        SystemClock.elapsedRealtime()
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
