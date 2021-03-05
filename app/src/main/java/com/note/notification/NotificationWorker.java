package com.note.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.note.R;
import com.note.note.Nota;

import java.util.concurrent.TimeUnit;


/**
 * This class provides a Notification System.
 *
 * Constructor is called by OneTimeRequestWorker#Builder(NotificationWorker.class),
 * it also enqueue the request with a trigger delay as the difference between
 * the current date and the pushing of the notification one, in millis.
 *
 * When called, it also passes info about date, time, title and description about note
 *
 *
 * @see androidx.work.Worker
 */
public class NotificationWorker extends Worker {

    private Context context;
    private WorkerParameters workerParameters;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.workerParameters = workerParams;
    }

    /**
     * Method called when the trigger delay is passed.
     * Notification is pushed and shows own info
     */
    @NonNull
    @Override
    public Result doWork() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "noteChannel")
                .setSmallIcon(R.mipmap.logo)

                .setColor(Color.RED)
                .setVibrate(new long[]{200, 100, 200, 100, 200, 100, 200})

                .setContentTitle(workerParameters.getInputData().getString("title"))
                .setContentInfo(workerParameters.getInputData().getString("date") +
                        " at " + workerParameters.getInputData().getString("time"))
                .setContentText(workerParameters.getInputData().getString("description"))
                .setDefaults(Notification.DEFAULT_SOUND);



        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(283, builder.build());

        return Result.success();
    }

    /**
     * Since Android O (API 26), a channel for notifications
     * must be set to the Notification Manager
     *
     * For more details see its doc
     * @see NotificationChannel
     *
     * This method is called by MainActivity#onCreate()
     */
    public static void createNotificationChannel(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "NoteChannel";
            String description = "Channel for Note notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("note", name, importance);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);

            channel.setVibrationPattern(new long[]{400, 100, 400, 100, 200, 100, 200});
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Schedule the new notification delayed as the difference
     * between the current date in millis and the notification's
     * @see Nota#getDelay()
     *
     * Passes to the constructor of NotficationWorker, called by builder,
     * date, time, title and description about @nota
     *
     * @param nota
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setNewNotification(Nota nota, Context context){

        WorkManager workManager = WorkManager.getInstance(context);

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(nota.getDelay(), TimeUnit.MILLISECONDS)
                .addTag(nota.getDate() + "@" + nota.getTime() + ".txt")
                .setInputData(new Data.Builder()
                        .putString("date", nota.getDate())
                        .putString("time", nota.getTime())
                        .putString("title", nota.getTitle())
                        .putString("description", nota.getNote()).build())
                .build();

        workManager.enqueue(request);
    }
}