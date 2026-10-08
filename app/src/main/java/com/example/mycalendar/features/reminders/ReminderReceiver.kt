package com.example.mycalendar.features.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mycalendar.MainActivity
import com.example.mycalendar.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("EXTRA_TASK_ID", -1)
        val taskTitle = intent.getStringExtra("EXTRA_TASK_TITLE") ?: "Task Reminder"
        val reminderType = intent.getStringExtra("EXTRA_REMINDER_TYPE") ?: "EXACT"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_channel",
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for task reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_TASK_ID", taskId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Меняем тексты в зависимости от типа будильника
        val notificationTitle = if (reminderType == "5_MIN") "Upcoming Task!" else "Task Started!"
        val notificationText = if (reminderType == "5_MIN") "Starts in 5 minutes: $taskTitle" else "It's time for: $taskTitle"

        val notification = NotificationCompat.Builder(context, "task_channel")
            .setSmallIcon(R.drawable.rocket_transparent)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Используем taskId как номер уведомления.
        // Это значит, что если человек не смахнул уведомление за 5 минут,
        // то при старте задачи оно просто обновится на новое (с текстом "Task Started!").
        notificationManager.notify(taskId, notification)
    }
}