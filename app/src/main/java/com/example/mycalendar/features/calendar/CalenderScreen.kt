package com.example.mycalendar.features.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycalendar.features.addtask.AddTaskViewModel
import com.example.mycalendar.ui.components.TopNavigationRow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Твои кастомные цвета
val CardYellow = Color(0xFFE3B876)
val TextYellowDark = Color(0xFF654321)
val CardBlueGray = Color(0xFFADBBBE)
val PillBlueGray = Color(0xFF3B4747)
val CardPurple = Color(0xFFBAB2CA)
val PillPurple = Color(0xFF453B5F)
val CardPink = Color(0xFFCA9BA3)
val PillPink = Color(0xFF5B2126)
val CardTeal = Color(0xFF9CCAC8)
val PillTeal = Color(0xFF0F6A65)
val CardLime = Color(0xFFBECA9D)
val PillLime = Color(0xFF3C4912)

// Массив для чередования цветов
val CalendarColors = listOf(
    Pair(CardYellow, TextYellowDark),
    Pair(CardBlueGray, PillBlueGray),
    Pair(CardPurple, PillPurple),
    Pair(CardPink, PillPink),
    Pair(CardTeal, PillTeal),
    Pair(CardLime, PillLime)
)

@Composable
fun CalenderScreen(
    currentTab: String,
    onTabChange: (String) -> Unit,
    onAddClick: () -> Unit,
    // Внедряем ViewModel шторки для передачи времени
    addTaskViewModel: AddTaskViewModel = hiltViewModel(),
    calendarViewModel: CalendarViewModel  = hiltViewModel()
) {
    // Состояние текущего месяца (по умолчанию месяц на устройстве)
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = remember { LocalDate.now() }

    // Генерируем все дни выбранного месяца
    val daysInMonth = remember(selectedMonth) {
        (1..selectedMonth.lengthOfMonth()).map { selectedMonth.atDay(it) }
    }

    // Подписываемся на список задач из базы данных
    val tasks by calendarViewModel.tasks.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3E3E3))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(48.dp))
                .background(Color.White)
                .padding(start = 8.dp, end = 8.dp, top = 20.dp, bottom = 32.dp)
        ) {
            TopNavigationRow(
                selectedTab = currentTab,
                onTabSelected = onTabChange,
                onAddClick = onAddClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Колесо выбора месяца
            MonthSelector(
                selectedMonth = selectedMonth,
                onMonthSelected = { selectedMonth = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            val visibleDays = remember(daysInMonth, today) {
                daysInMonth.filter { !it.isBefore(today) }
            }
            // Вертикальный список дней с чередованием цветов
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                itemsIndexed(visibleDays) { index, date ->
                    // Вычисляем цвет по индексу
                    val colorPair = CalendarColors[index % CalendarColors.size]

                    // Пропускаем дни, которые уже прошли (если смотрим текущий месяц)
                    if (date.isBefore(today)) return@itemsIndexed

                    // Ищем задачи, которые привязаны именно к этой дате
                    val tasksForThisDay = tasks.filter { task ->
                        val taskDate = Instant.ofEpochMilli(task.startTime)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        taskDate == date
                    }

                    // Если на этот день есть задачи — отрисовываем их карточки
                    if (tasksForThisDay.isNotEmpty()) {

                        DayTimelineCard(
                            date = date,
                            isToday = date == today,
                            backgroundColor = colorPair.first,
                            contentColor = colorPair.second,
                            tasksForDay = tasks.filter { task ->
                                val taskDate = Instant.ofEpochMilli(task.startTime)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                taskDate == date
                            },
                            onAddSpecificTime = { time ->
                                addTaskViewModel.resetForNewTask()
                                val formatter = DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.ENGLISH)
                                val startDateTime = LocalDateTime.of(date, time)
                                val endDateTime = startDateTime.plusHours(1)

                                addTaskViewModel.updateStartDateTime(startDateTime.format(formatter))
                                addTaskViewModel.updateEndDateTime(endDateTime.format(formatter))
                                onAddClick()
                            },
                            onTaskClick = { taskId ->
                                // Загружаем задачу для редактирования и открываем шторку
                                addTaskViewModel.loadTaskForEditing(taskId)
                                onAddClick()
                            }
                        )

                    } else {
                        // Иначе показываем пустой таймлайн дня
                        DayTimelineCard(
                            date = date,
                            isToday = date == today,
                            backgroundColor = colorPair.first,
                            contentColor = colorPair.second,
                            onAddSpecificTime = { time ->
                                addTaskViewModel.resetForNewTask()
                                val formatter = DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.ENGLISH)
                                val startDateTime = LocalDateTime.of(date, time)
                                val endDateTime = startDateTime.plusHours(1)

                                addTaskViewModel.updateStartDateTime(startDateTime.format(formatter))
                                addTaskViewModel.updateEndDateTime(endDateTime.format(formatter))
                                onAddClick()
                            },
                            onTaskClick = {  }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}