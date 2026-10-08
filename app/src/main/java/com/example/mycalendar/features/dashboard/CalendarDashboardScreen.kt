package com.example.mycalendar.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycalendar.features.addtask.AddTaskViewModel
import com.example.mycalendar.features.reminders.RemindersBottomSheet
import com.example.mycalendar.ui.components.TopNavigationRow
import java.time.LocalDate

@Composable
fun CalendarDashboardScreen(
    currentTab: String,
    onTabChange: (String) -> Unit,
    onAddClick: () -> Unit,
    viewModel: DashboardViewModel,
    addTaskViewModel: AddTaskViewModel = hiltViewModel()
) {
    // Получаем текущую дату один раз при перерисовке
    val today = remember { LocalDate.now() }

    // Подписываемся на список задач из Room на сегодня
    val todaysTasks by viewModel.getTasksForDate(today).collectAsState(initial = emptyList())

    // Состояние для управления шторкой напоминаний
    var showRemindersSheet by remember { mutableStateOf(false) }

    val topZone by viewModel.topZoneId.collectAsState()
    val bottomZone by viewModel.bottomZoneId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE4E5E0))
            .statusBarsPadding() // Отступ от системной шторки (часы, батарея)
    ) {
        // Обертка для навигации и даты с отступами от краев экрана
        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 20.dp,
                top = 20.dp
            )
        ) {
            // Вызываем навигацию и прокидываем состояния наверх
            TopNavigationRow(
                selectedTab = currentTab,
                onTabSelected = onTabChange,
                onAddClick = {
                    // Важно: если нажимаем на плюсик в навигации,
                    // нужно очистить шторку для создания НОВОЙ задачи
                    addTaskViewModel.resetForNewTask()
                    onAddClick()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            DateAndTimeRow(
                topZoneId = topZone,
                bottomZoneId = bottomZone,
                onTopZoneChange = { viewModel.updateTopZone(it) },
                onBottomZoneChange = { viewModel.updateBottomZone(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Белая шторка с задачами займет весь низ
        TasksBottomSheet(
            tasks = todaysTasks,
            modifier = Modifier.weight(1f),
            onTaskClick = { taskId ->
                // Загружаем задачу для редактирования
                addTaskViewModel.loadTaskForEditing(taskId)
                // Открываем шторку
                onAddClick()
            },
            onRemindersClick = {
                showRemindersSheet = true
            }
        )
    }
    if (showRemindersSheet) {
        RemindersBottomSheet(
            onDismiss = { showRemindersSheet = false },
            viewModel = viewModel,
            onTaskClick = { taskId ->
                showRemindersSheet = false
                addTaskViewModel.loadTaskForEditing(taskId)
                onAddClick()
            }
        )
    }
}




