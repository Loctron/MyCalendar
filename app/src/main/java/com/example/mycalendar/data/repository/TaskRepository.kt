package com.example.mycalendar.data.repository

import com.example.mycalendar.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insertTask(task: TaskEntity): Long
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    fun getTasksForDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>
    fun getTasksForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<TaskEntity>>
    suspend fun getTaskById(id: Int): TaskEntity?
    fun getAllTasks(): Flow<List<TaskEntity>>
}