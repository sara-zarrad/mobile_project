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

public class CourseNotificationHelper {
    private static final String CHANNEL_ID = "course_notification_channel";
    private static final String CHANNEL_NAME = "Course Management";
    private static final int NOTIFICATION_ID = 1;

    @SuppressLint("ObsoleteSdkInt")
    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Notifications for course management";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static boolean canSendNotifications(Context context) {
        // For Android 13 and above, check POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }

        // For versions below Android 13, notifications are allowed by default
        return true;
    }

    public static void showCourseAddedNotification(Context context, String courseName) {
        // Check if notifications are permitted
        if (!canSendNotifications(context)) {
            return;
        }

        // Ensure notification channel is created
        createNotificationChannel(context);

        try {
            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.student)
                    .setContentTitle("New Course Added")
                    .setContentText("Course '" + courseName + "' has been successfully added.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            // Show the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            // Log the error or handle the permission issue
            android.util.Log.e("NotificationHelper", "Permission to post notifications denied", e);
        }
    }
}
