package com.example.mycalendar.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // --- ЗАПРОСЫ ДЛЯ ЭКРАНОВ ---

    // Для экрана "Today": получаем задачи, которые попадают в промежуток конкретного дня
    @Query("""
        SELECT * FROM tasks 
        WHERE startTime >= :startOfDay AND startTime <= :endOfDay 
        ORDER BY startTime ASC
    """)
    fun getTasksForDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    // Для экрана "Календарь": получаем задачи на весь месяц
    @Query("""
        SELECT * FROM tasks 
        WHERE startTime >= :startOfMonth AND startTime <= :endOfMonth 
        ORDER BY startTime ASC
    """)
    fun getTasksForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<TaskEntity>>

    // Для деталей задачи: получить одну задачу по ID
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    // Для получения всех задач
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

}