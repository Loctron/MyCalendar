package com.example.mycalendar.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycalendar.data.local.TaskEntity
import com.example.mycalendar.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    // Верхний часовой пояс (по умолчанию системный устройства)
    private val _topZoneId = MutableStateFlow(ZoneId.systemDefault())
    val topZoneId: StateFlow<ZoneId> = _topZoneId.asStateFlow()

    // Нижний часовой пояс (по умолчанию Новосибирск)
    private val _bottomZoneId = MutableStateFlow(ZoneId.of("Asia/Novosibirsk"))
    val bottomZoneId: StateFlow<ZoneId> = _bottomZoneId.asStateFlow()

    fun updateTopZone(zoneId: ZoneId) {
        _topZoneId.value = zoneId
    }

    fun updateBottomZone(zoneId: ZoneId) {
        _bottomZoneId.value = zoneId
    }

    // Функция возвращает Flow со списком задач для конкретного дня
    fun getTasksForDate(date: LocalDate): Flow<List<TaskEntity>> {
        val zoneId = ZoneId.of("Europe/Moscow")

        // Получаем начало дня (00:00:00) в миллисекундах
        val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()

        // Получаем конец дня (23:59:59) в миллисекундах
        val endOfDay = date.plusDays(1).atStartOfDay(zoneId).minusNanos(1).toInstant().toEpochMilli()

        return repository.getTasksForDay(startOfDay, endOfDay)
    }

    // Получаем все задачи из базы в реальном времени
    val tasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}