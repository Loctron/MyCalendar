package com.example.mycalendar.features.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

@Composable
fun TimeSlotColumn(
    time: String,
    taskName: String? = null,
    colorUse: Color,
    onAddClick: () -> Unit,
    backgroundColor: Color,
    onTaskClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(end = 16.dp)
    ) {
        // --- ЛЕВАЯ КОЛОНКА: Линия ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(90.dp) // Вертикальная линия
                    .background(colorUse.copy(alpha = 0.5f))
            )
        }

        // --- ПРАВАЯ КОЛОНКА: Время и Задачи ---
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelMedium,
                color = colorUse
            )

            // Отрисовка плашки задачи, если она есть
            if (taskName != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorUse)
                        .clickable { onTaskClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = taskName,
                        color = backgroundColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Маленький плюс, но с расширенной зоной клика
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onAddClick() }
                    .padding(4.dp), // Делает кликабельную область чуть больше
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(1.dp, colorUse.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorUse.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}