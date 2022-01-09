package org.o7planning.dak19.CloudMess;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class MyApplication extends ContextWrapper {
    public static final String CHANNEL_ID = "push_notification_message";

    public MyApplication(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            createChannelNotification();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannelNotification() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "PushNotificationMessage", NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if(manager!=null) {
            manager.createNotificationChannel(channel);
        }

    }

}
