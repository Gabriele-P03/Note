package com.note.notification;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.note.R;
import com.note.note.Nota;


/**
 * This class provides a Notification System.
 *
 * Constructor is called by OneTimeRequestWorker#Builder(NotificationWorker.class),
 * it also enqueue the request with a trigger delay as the difference between
 * the current date and the pushing of the notification one, in millis.
 *
 * When called, it also passes info about date, time, title and description about note
 * @see com.note.activity.NoteActivity#setNewNotification(Nota)
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
                .setContentText(workerParameters.getInputData().getString("description"));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(283, builder.build());

        return Result.success();
    }
}