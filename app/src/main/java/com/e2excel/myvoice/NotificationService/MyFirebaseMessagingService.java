package com.e2excel.myvoice.NotificationService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.e2excel.myvoice.GlobalValues.PublicClass;
import com.e2excel.myvoice.R;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /*
    SharedPreferences pref = getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();

    */
//    Context ctx = getApplicationContext();
    public static SharedPreferences preferences;


    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("FCM_PREF", MODE_PRIVATE);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.e2excel.myvoice";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("MyVoice Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notiBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notiBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.v("TokenTag", s);
        PublicClass.FCM_TOKEN = s;
        if (!"".equals(s) || s != null) {
            MyFirebaseMessagingService.preferences.edit().putString("fcm_token", s).commit();

            Log.v("fcm_token", "fcm_token(in service class) enrty , FCm token overwrite to this :"+s);
        }
        else{
            Log.v("fcm_token", "fcm_token(in service class) enrty , FCM has received null or empty; so I have not write it in a FCM_PREF!~rohan");
        }


    }
}
