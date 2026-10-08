package com.example.mycalendar.features.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycalendar.data.local.TaskEntity
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DayTimelineCard(
    date: LocalDate,
    isToday: Boolean,
    backgroundColor: Color,
    contentColor: Color,
    onAddSpecificTime: (LocalTime) -> Unit,
    tasksForDay: List<TaskEntity> = emptyList(),
    onTaskClick: (Int) -> Unit
) {
    //  Форматируем дату для левой части
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val dayOfMonth = date.dayOfMonth.toString()
    val monthStr = date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase()
    val dateText = "$dayOfMonth\n$monthStr"

    // Превращаем список задач в словарь вида: { Час -> Pair(ID, Название) }
    val tasksMap = remember(tasksForDay) {
        val map = mutableMapOf<Int, Pair<Int, String>>()

        tasksForDay.forEach { task ->
            val startZdt = Instant.ofEpochMilli(task.startTime).atZone(ZoneId.systemDefault())
            val endZdt = Instant.ofEpochMilli(task.endTime).atZone(ZoneId.systemDefault())

            val startHour = startZdt.hour
            val endHour = endZdt.hour
            val endMinute = endZdt.minute

            // Вычисляем последний час, который занимает задача
            // Если задача заканчивается ровно в 18:00, слот 18:00-19:00 должен быть свободен (endHour - 1)
            // Если заканчивается в 18:30, то слот 18:00-19:00 занят (endHour)
            var lastHour = if (endHour > startHour && endMinute == 0) {
                endHour - 1
            } else {
                endHour
            }

            // На случай, если задача идет до следующего дня (например, до 02:00 ночи)
            if (endZdt.toLocalDate().isAfter(startZdt.toLocalDate())) {
                lastHour = 23
            }

            // Заполняем плашкой все часы между стартом и концом задачи
            for (hour in startHour..lastHour) {
                map[hour] = Pair(task.id, task.title)
            }
        }

        map // Возвращаем готовую карту
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // --- ЛЕВАЯ ЧАСТЬ: ДАТА ---
        Column(
            modifier = Modifier.padding(end = 24.dp)
        ) {
            Text(
                text = dayOfWeek,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dateText,
                style = MaterialTheme.typography.displayMedium.copy(lineHeight = 40.sp),
                color = contentColor
            )
        }

        // --- ПРАВАЯ ЧАСТЬ: ТАЙМЛАЙН ---
        // Если сегодня — начинаем с текущего часа, иначе — с 00:00
        val startHour = if (isToday) LocalTime.now().hour else 0
        val hours = remember(startHour) { (startHour..23).map { LocalTime.of(it, 0) } }
        val timeFormatter = remember { DateTimeFormatter.ofPattern("h a", Locale.ENGLISH) }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(top = 28.dp) // Точно выравниваем по цифре даты
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.Top
            ) {
                items(hours) { time ->
                    val taskPair = tasksMap[time.hour] // Получаем Pair(id, title)

                    TimeSlotColumn(
                        time = time.format(timeFormatter).lowercase(), // "3 pm"
                        taskName =  taskPair?.second,
                        colorUse = contentColor,
                        onAddClick = { onAddSpecificTime(time) },
                        backgroundColor = backgroundColor,
                        onTaskClick = {
                            taskPair?.first?.let { id -> onTaskClick(id) } // Передаем ID при клике
                        }
                    )
                }
            }
        }
    }
}