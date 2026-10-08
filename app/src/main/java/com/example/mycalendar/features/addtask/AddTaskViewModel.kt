package com.example.mycalendar.features.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycalendar.data.local.TaskEntity
import com.example.mycalendar.data.repository.TaskRepository
import com.example.mycalendar.features.reminders.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale
import javax.inject.Inject

// Обновляем модель состояния
data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val isUrgent: Boolean = false,
    val isImportant: Boolean = false,
    val isReminderEnabled: Boolean = false,
    val startDateTimeStr: String = "", // Строка для старта
    val endDateTimeStr: String = "",    // Строка для конца
    val isEditing: Boolean = false
)

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val alarmScheduler: AlarmScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    private var currentEditingTaskId: Int? = null

    // Инициализируем дефолтное время при создании ViewModel
    init {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a", Locale.ENGLISH)
        val now = LocalDateTime.now()

        _uiState.update {
            it.copy(
                startDateTimeStr = now.format(formatter),
                endDateTimeStr = now.plusHours(1).format(formatter)
            )
        }
    }

    // --- Функции для обновления состояния из UI ---

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun updatePriority(isUrgent: Boolean, isImportant: Boolean) {
        _uiState.update { it.copy(isUrgent = isUrgent, isImportant = isImportant) }
    }

    fun toggleReminder(isEnabled: Boolean) {
        _uiState.update { it.copy(isReminderEnabled = isEnabled) }
    }

    // Две отдельные функции для времени
    fun updateStartDateTime(newDateTime: String) {
        _uiState.update { it.copy(startDateTimeStr = newDateTime) }
    }

    fun updateEndDateTime(newDateTime: String) {
        _uiState.update { it.copy(endDateTimeStr = newDateTime) }
    }

    // --- Главная функция сохранения ---

    fun saveTask(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.title.isBlank()) return

        viewModelScope.launch {
            val realStartTime = parseDateStringToMillis(currentState.startDateTimeStr)
            val realEndTime = parseDateStringToMillis(currentState.endDateTimeStr)

            if (currentEditingTaskId != null) {
                // РЕДАКТИРОВАНИЕ существующей задачи
                val updatedTask = TaskEntity(
                    id = currentEditingTaskId!!,
                    title = currentState.title.trim(),
                    description = currentState.description.trim(),
                    startTime = realStartTime,
                    endTime = realEndTime,
                    isUrgent = currentState.isUrgent,
                    isImportant = currentState.isImportant,
                    isReminderEnabled = currentState.isReminderEnabled
                )
                repository.updateTask(updatedTask)
                alarmScheduler.schedule(updatedTask)
            } else {
                // СОЗДАНИЕ новой задачи
                val newTask = TaskEntity(
                    title = currentState.title.trim(),
                    description = currentState.description.trim(),
                    startTime = realStartTime,
                    endTime = realEndTime,
                    isUrgent = currentState.isUrgent,
                    isImportant = currentState.isImportant,
                    isReminderEnabled = currentState.isReminderEnabled
                )
                val generatedId = repository.insertTask(newTask).toInt()
                val taskWithId = newTask.copy(id = generatedId)
                alarmScheduler.schedule(taskWithId)
            }

            onSuccess()
        }
    }

    // Вспомогательная функция для парсинга нашей кастомной строки
    private fun parseDateStringToMillis(dateStr: String): Long {
        return try {
            // "d" вместо "dd" позволяет читать и "08" и "8"
            val formatter = DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("MMM d, h:mm a")
                .parseDefaulting(ChronoField.YEAR, LocalDateTime.now().year.toLong())
                .toFormatter(Locale.ENGLISH)

            val localDateTime = LocalDateTime.parse(dateStr, formatter)
            localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: Exception) {
            e.printStackTrace()
            System.currentTimeMillis()
        }
    }

    fun loadTaskForEditing(taskId: Int) {
        currentEditingTaskId = taskId
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                val formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a", Locale.ENGLISH)

                val startStr = Instant.ofEpochMilli(task.startTime)
                    .atZone(ZoneId.systemDefault())
                    .format(formatter)

                val endStr = Instant.ofEpochMilli(task.endTime)
                    .atZone(ZoneId.systemDefault())
                    .format(formatter)

                _uiState.update {
                    it.copy(
                        title = task.title,
                        description = task.description,
                        isUrgent = task.isUrgent,
                        isImportant = task.isImportant,
                        isReminderEnabled = task.isReminderEnabled,
                        startDateTimeStr = startStr,
                        endDateTimeStr = endStr,
                        isEditing = true
                    )
                }
            }
        }
    }

    // Функция очистки
    fun resetForNewTask() {
        currentEditingTaskId = null
        val formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a", Locale.ENGLISH)
        val now = LocalDateTime.now()
        _uiState.update {
            it.copy(
                title = "",
                description = "",
                isUrgent = false,
                isImportant = false,
                isReminderEnabled = false,
                startDateTimeStr = now.format(formatter),
                endDateTimeStr = now.plusHours(1).format(formatter),
                isEditing = false
            )
        }
    }

    fun deleteTask(onSuccess: () -> Unit) {
        val taskId = currentEditingTaskId ?: return
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                repository.deleteTask(task)

                // Отменяем системный будильник при удалении задачи из БД
                alarmScheduler.cancel(taskId)
            }
            onSuccess()
        }
    }
}