package com.example.mycalendar.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mycalendar.data.local.TaskEntity
import com.example.mycalendar.ui.components.TaskCard
import com.example.mycalendar.ui.theme.AppTypography
import com.example.mycalendar.ui.theme.CardBlueGray
import com.example.mycalendar.ui.theme.CardLime
import com.example.mycalendar.ui.theme.CardPink
import com.example.mycalendar.ui.theme.CardPurple
import com.example.mycalendar.ui.theme.CardTeal
import com.example.mycalendar.ui.theme.CardYellow
import com.example.mycalendar.ui.theme.PillBlueGray
import com.example.mycalendar.ui.theme.PillLime
import com.example.mycalendar.ui.theme.PillPink
import com.example.mycalendar.ui.theme.PillPurple
import com.example.mycalendar.ui.theme.PillTeal
import com.example.mycalendar.ui.theme.TextYellowDark

val TaskColors = listOf(
    Pair(CardYellow, TextYellowDark),
    Pair(CardBlueGray, PillBlueGray),
    Pair(CardPurple, PillPurple),
    Pair(CardPink, PillPink),
    Pair(CardTeal, PillTeal),
    Pair(CardLime, PillLime)
)
@Composable
fun TasksBottomSheet(
    modifier: Modifier = Modifier,
    tasks: List<TaskEntity>,
    onTaskClick: (Int) -> Unit,
    onRemindersClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)) // Скругляем верх
            .background(Color.White) // Обязательно красим в белый, чтобы отличить от серого фона
            .padding(
                top = 32.dp,
                end = 8.dp,
                start = 8.dp,
                bottom = 24.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp
                ),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Todays tasks",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFF4F6F5))
                    .clickable { onRemindersClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "Reminders", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (tasks.isEmpty()) {
            // Заглушка, если на сегодня нет задач
            Text(
                text = "No tasks for today. Take a rest!",
                style = AppTypography.titleLarge,
                color = Color.Gray,
                modifier = Modifier.padding(top = 20.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(tasks) { index, task ->
                    // Вычисляем цвет на основе индекса карточки в списке
                    val colorPair = TaskColors[index % TaskColors.size]

                    // Вызов UI-компонента карточки
                    TaskCard(
                        task = task,
                        backgroundColor = colorPair.first,
                        contentColor = colorPair.second,
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}
