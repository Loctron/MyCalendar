package com.example.mycalendar.di

import android.content.Context
import androidx.room.Room
import com.example.mycalendar.data.local.AppDatabase
import com.example.mycalendar.data.local.TaskDao
import com.example.mycalendar.data.repository.TaskRepository
import com.example.mycalendar.data.repository.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Модуль будет жить всё время, пока открыто приложение
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "calendar_database" // Имя файла базы данных в памяти телефона
        ).build()
    }

    // Получение TaskDao из базы данных
    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    // Когда кто-то просит интерфейс TaskRepository,
    // отдаётся реализация TaskRepositoryImpl
    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }
}