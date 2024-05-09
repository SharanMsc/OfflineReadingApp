package com.dreamworks.offlinereading.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dreamworks.offlinereading.screens.MainActivity
import com.dreamworks.offlinereading.R
class ReminderReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // unique id for the channel
            val channelId = "medicine_reminders"

            // name of the channel
            val channelName = "Medicine Reminders"

            // importance level of the channel
            val importance = NotificationManager.IMPORTANCE_HIGH

            // creating channel
            val channel =
                NotificationChannel(channelId, channelName, importance)

            // enabling lights for the channel
            channel.enableLights(true)

            // setting light color for the channel
            channel.lightColor = Color.RED

            // enabling vibration for the channel
            channel.enableVibration(true)
            val notificationManager = p0!!.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                channel
            ) // registering the channel with the system
        }

        // Create the notification

        // unique id for the notification
        val notificationId = 1

        // title of the notification
        val title = "Medicine Reminder"

        // text of the notification
        val text = "It's time to take your medicine."
        val intent =
            Intent(
                p0,
                MainActivity::class.java
            ) // intent to launch when the notification is clicked
        val pendingIntent =
            PendingIntent.getActivity(
                p0,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            ) // creating a pending intent to wrap the intent

        // creating the builder object
        val builder =
            NotificationCompat.Builder(p0!!, "medicine_reminders")
                .setSmallIcon(
                    R.drawable.ic_notifications_black_24dp
                ) // setting the small icon for the notification
                .setContentTitle(title) // setting the title for the notification
                .setContentText(text) // setting the text for the notification
                .setContentIntent(pendingIntent) // attaching the pending intent to the notification
                .setAutoCancel(
                    true
                ) // setting the notification to be automatically cancelled when clicked
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                ) // setting the priority level of the notification

        // Display the notification

        // getting the notification manager
        val notificationManager =
            NotificationManagerCompat.from(p0)

        // displaying the notification
        if (ActivityCompat.checkSelfPermission(
                p0,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(notificationId, builder.build())
    }
}