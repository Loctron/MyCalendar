package com.example.mycalendar.features.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.mycalendar.data.local.TaskEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(task: TaskEntity) {
        if (!task.isReminderEnabled) return

        val timeExact = task.startTime
        val time5MinBefore = timeExact - (5 * 60 * 1000)

        val now = System.currentTimeMillis()
        val gracePeriod = 60 * 1000 // 1 минута "прощения" на задержку при сохранении

        // 1. Устанавливаем будильник ЗА 5 МИНУТ
        if (time5MinBefore > (now - gracePeriod)) {
            val intent5Min = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("EXTRA_TASK_ID", task.id)
                putExtra("EXTRA_TASK_TITLE", task.title)
                putExtra("EXTRA_REMINDER_TYPE", "5_MIN")
            }

            val pendingIntent5Min = PendingIntent.getBroadcast(
                context,
                task.id * 10,
                intent5Min,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            scheduleSafely(time5MinBefore, pendingIntent5Min)
        }

        // 2. Устанавливаем будильник РОВНО В СТАРТ
        if (timeExact > (now - gracePeriod)) {
            val intentExact = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("EXTRA_TASK_ID", task.id)
                putExtra("EXTRA_TASK_TITLE", task.title)
                putExtra("EXTRA_REMINDER_TYPE", "EXACT")
            }

            val pendingIntentExact = PendingIntent.getBroadcast(
                context,
                task.id * 10 + 1,
                intentExact,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            scheduleSafely(timeExact, pendingIntentExact)
        }
    }

    // Умная функция установки будильника, которая обходит блокировки новых Android
    private fun scheduleSafely(triggerTime: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Если Android 12+ и точные будильники разрешены
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    // Фолбэк: если пользователь не дал права на точные будильники, ставим обычный
                    // Он сработает, но система может задержать его на пару минут для экономии батареи
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    Log.w("AlarmScheduler", "Exact alarms not permitted. Using inexact alarm.")
                }
            } else {
                // Для старых Android всё работает как раньше
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            // На самый крайний случай
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancel(taskId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)

        val pendingIntent5Min = PendingIntent.getBroadcast(
            context,
            taskId * 10,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent5Min)

        val pendingIntentExact = PendingIntent.getBroadcast(
            context,
            taskId * 10 + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntentExact)
    }
}