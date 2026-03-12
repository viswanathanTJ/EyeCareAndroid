package com.viswa2k.eyecare.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.viswa2k.eyecare.MainActivity
import com.viswa2k.eyecare.R

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        val monitoringChannel = NotificationChannel(
            MONITORING_CHANNEL_ID,
            "Eye Care Monitoring",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when eye care monitoring is active"
        }

        val breakChannel = NotificationChannel(
            BREAK_CHANNEL_ID,
            "Break Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminds you to take eye breaks"
        }

        notificationManager.createNotificationChannels(listOf(monitoringChannel, breakChannel))
    }

    fun buildMonitoringNotification(remainingMinutes: Int, remainingSeconds: Int): Notification {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeText = String.format("%02d:%02d", remainingMinutes, remainingSeconds)
        return NotificationCompat.Builder(context, MONITORING_CHANNEL_ID)
            .setContentTitle("Eye Care Active")
            .setContentText("Next break in $timeText")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    fun buildBreakNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, BREAK_CHANNEL_ID)
            .setContentTitle("Time for an Eye Break!")
            .setContentText("Look at something 20 feet away for 20 seconds")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }

    fun updateMonitoringNotification(remainingMinutes: Int, remainingSeconds: Int) {
        notificationManager.notify(
            MONITORING_NOTIFICATION_ID,
            buildMonitoringNotification(remainingMinutes, remainingSeconds)
        )
    }

    fun showBreakNotification() {
        notificationManager.notify(BREAK_NOTIFICATION_ID, buildBreakNotification())
    }

    companion object {
        const val MONITORING_CHANNEL_ID = "eye_care_monitoring"
        const val BREAK_CHANNEL_ID = "eye_care_break"
        const val MONITORING_NOTIFICATION_ID = 1
        const val BREAK_NOTIFICATION_ID = 2
    }
}
