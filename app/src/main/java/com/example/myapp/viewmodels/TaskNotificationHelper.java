package com.example.myapp.viewmodels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapp.R;

public class TaskNotificationHelper {
    private static final String CHANNEL_ID = "task_notification_channel";
    private static final String CHANNEL_NAME = "Task Management";
    private static final int NOTIFICATION_ID = 2;

    /**
     * Creates a notification channel for devices running Android Oreo or later.
     *
     * @param context The application context.
     */
    @SuppressLint("ObsoleteSdkInt")
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Notifications for task management";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Checks if the app can send notifications (Android 13+ requires permission).
     *
     * @param context The application context.
     * @return True if the app has permission to send notifications; false otherwise.
     */
    public static boolean canSendNotifications(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Displays a notification for a new task with a specified date and time.
     *
     * @param context      The application context.
     * @param taskName     The name of the task.
     * @param taskDateTime
     */
    public static void showTaskAddedNotification(Context context, String taskName, String taskDateTime) {
        if (!canSendNotifications(context)) {
            return;
        }

        createNotificationChannel(context);

        try {
            // Format the notification message
            String notificationMessage = "Task '" + taskName + "' is scheduled for " + taskDateTime + ".";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.student) // Update with a valid icon
                    .setContentTitle("Task Reminder")
                    .setContentText(notificationMessage)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage)) // For longer text
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            android.util.Log.e("TaskNotificationHelper", "Permission to post notifications denied", e);
        }
    }
}
