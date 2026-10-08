package com.example.mycalendar.data.repository

import com.example.mycalendar.data.local.TaskDao
import com.example.mycalendar.data.local.TaskEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun insertTask(task: TaskEntity): Long {
        return taskDao.insertTask(task)
    }

    override suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    override fun getTasksForDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>> {
        return taskDao.getTasksForDay(startOfDay, endOfDay)
    }

    override fun getTasksForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<TaskEntity>> {
        return taskDao.getTasksForMonth(startOfMonth, endOfMonth)
    }

    override suspend fun getTaskById(id: Int): TaskEntity? {
        return taskDao.getTaskById(id)
    }

    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }
}