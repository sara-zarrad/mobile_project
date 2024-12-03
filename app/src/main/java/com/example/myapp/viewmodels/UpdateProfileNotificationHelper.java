package com.example.myapp.viewmodels;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapp.R;

public class UpdateProfileNotificationHelper {
    private static final String CHANNEL_ID = "update_profile_channel";
    private static final String CHANNEL_NAME = "Profile Updates";

    /**
     * Creates a notification channel for profile updates.
     * This method checks for Android version compatibility.
     */
    @SuppressLint("ObsoleteSdkInt")
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Notifications for profile updates";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Checks if the app has permission to send notifications on API level 33 and above.
     *
     * @param context The application context.
     * @return true if permission is granted, false otherwise.
     */
    private static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33 and above
            return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // No need for permission check on lower API levels
    }

    /**
     * Shows a notification for profile updates.
     *
     * @param context      The application context.
     * @param updatedField The field that was updated (e.g., "Password", "Email", "Username").
     */
    @SuppressLint("MissingPermission")
    public static void showProfileUpdateNotification(Context context, String updatedField) {
        // Check for notification permission on API level 33+
        if (hasNotificationPermission(context)) {
            createNotificationChannel(context);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_edit) // Ensure you have a valid icon in res/drawable
                    .setContentTitle("Profile Updated")
                    .setContentText("Your " + updatedField + " has been successfully updated.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Generate a unique notification ID for each notification
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, builder.build());
        } else {
            // Show a toast or handle the case where permission is denied
            Toast.makeText(context, "Notification permission is required to show notifications.", Toast.LENGTH_SHORT).show();
        }
    }
}
