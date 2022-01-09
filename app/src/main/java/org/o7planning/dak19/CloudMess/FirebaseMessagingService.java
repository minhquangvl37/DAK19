package org.o7planning.dak19.CloudMess;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;
import android.window.SplashScreen;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.RemoteMessage;

import org.o7planning.dak19.activities.CustomerActivity;
import org.o7planning.dak19.activities.MainAdminActivity;
import org.o7planning.dak19.activities.MainSellerActivity;
import org.o7planning.dak19.activities.OrderDetailsAdminActivity;

import java.util.Date;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

     NotificationManager mNotificationManager;

    @RequiresApi(api = 31)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sent = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null && sent.equals(firebaseUser.getUid())){
            sendNotification(remoteMessage);
        }
    }


    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String type = remoteMessage.getData().get("type");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent;

        if(type.equals("customer"))
            intent = new Intent(this, MainAdminActivity.class); //cho tao coi cái adapter của cái đó
        else
            intent = new Intent(this, MainSellerActivity.class); //cho tao coi cái adapter của cái đó

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MyApplication myApplication = new MyApplication(this);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(getNotificationId(), builder.build());

    }
    private int getNotificationId(){return (int) new Date().getTime();}
// playing audio and vibration when user se reques
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        r.play();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            r.setLooping(false);
//        }

        // vibration
//        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        long[] pattern = {100, 300, 300, 300};
//        v.vibrate(pattern, -1);
//
//
//        int resourceImage = getResources().getIdentifier(remoteMessage.getNotification().getIcon(), "drawable", getPackageName());
//
//
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            builder.setSmallIcon(R.drawable.icontrans);
//            builder.setSmallIcon(resourceImage);
//        } else {
////            builder.setSmallIcon(R.drawable.icon_kritikar);
//            builder.setSmallIcon(resourceImage);
//        }
//
//
//
//        Intent resultIntent = new Intent(this, SplashScreen.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        builder.setContentTitle(remoteMessage.getNotification().getTitle());
//        builder.setContentText(remoteMessage.getNotification().getBody());
//        builder.setContentIntent(pendingIntent);
//        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
//        builder.setAutoCancel(true);
//        builder.setPriority(Notification.PRIORITY_MAX);
//
//        mNotificationManager =
//                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//        {
//            String channelId = "Your_channel_id";
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_HIGH);
//            mNotificationManager.createNotificationChannel(channel);
//            builder.setChannelId(channelId);
//        }
//
//
//
//// notificationId is a unique int for each notification that you must define
//        mNotificationManager.notify(100, builder.build());
//
//
//    }

}



