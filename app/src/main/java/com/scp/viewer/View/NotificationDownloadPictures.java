package com.scp.viewer.View;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.scp.viewer.R;


public class NotificationDownloadPictures {

    private static final String CHANNEL_ID = "CHANNEL_BF_LANGUAGE_DOWNLOAD";

    /**
     * Create channel
     * @param context context
     */
    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null,null);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;

            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void createNotification(final Context context, int max, int curent, String status, String title, int idNotification){

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(status)
                .setSmallIcon(R.drawable.ic_file_download_white_24dp)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Issue the initial notification with zero progress Download in progress
        builder.setAutoCancel(false);
        builder.setProgress(max, curent, false);
        createNotificationChannel(context);
        notificationManager.notify(idNotification, builder.build());

    }

}
