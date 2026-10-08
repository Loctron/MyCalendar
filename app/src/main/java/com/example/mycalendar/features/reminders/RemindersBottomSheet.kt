package com.example.mycalendar.features.reminders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mycalendar.features.dashboard.DashboardViewModel
import com.example.mycalendar.ui.components.TaskCard
import com.example.mycalendar.ui.theme.AppTypography
import com.example.mycalendar.features.dashboard.TaskColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersBottomSheet(
    onDismiss: () -> Unit,
    viewModel: DashboardViewModel,
    onTaskClick: (Int) -> Unit
) {
    // Берем все задачи из базы
    val allTasks by viewModel.tasks.collectAsState()

    // Оставляем только те, где включено напоминание и время начала еще не прошло
    val upcomingReminders = remember(allTasks) {
        val currentTime = System.currentTimeMillis()
        allTasks
            .filter { it.isReminderEnabled && it.startTime > currentTime }
            .sortedBy { it.startTime } // Сортируем от ближайших к дальним
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF4F6F5),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "UPCOMING REMINDERS",
                style = AppTypography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (upcomingReminders.isEmpty()) {
                Text(
                    text = "No scheduled reminders.",
                    style = AppTypography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 48.dp)
                ) {
                    itemsIndexed(upcomingReminders) { index, task ->
                        val colorPair = TaskColors[index % TaskColors.size]

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
}