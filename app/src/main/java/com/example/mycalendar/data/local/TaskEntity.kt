package com.example.mycalendar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // 0 означает, что Room сам сгенерирует уникальный ID

    val title: String,
    val description: String,

    val startTime: Long, // Время начала (Timestamp в миллисекундах)
    val endTime: Long,   // Время окончания

    val isUrgent: Boolean,    // true = Ракета (Срочно), false = Ветер (Не срочно)
    val isImportant: Boolean, // true = Бриллиант (Важно), false = Лист (Не важно)

    val isReminderEnabled: Boolean
)
