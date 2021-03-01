package com.note.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.note.R;

public class NotificationWorker extends BroadcastReceiver {
    

    @Override
    public void onReceive(Context context, Intent intent) {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "note")
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentInfo(intent.getStringExtra("date") + " at " + intent.getStringExtra("time"))
                .setContentText(intent.getStringExtra("note"));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(283, builder.build());
    }

}
