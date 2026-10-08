package com.example.mycalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycalendar.data.local.TaskEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.mycalendar.R

@Composable
fun TaskCard(
    task: TaskEntity,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    // Конвертируем миллисекунды из базы обратно в читаемое время (например, "4:20 PM")
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)

    val startTimeStr = Instant.ofEpochMilli(task.startTime)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
        .format(timeFormatter)

    val endTimeStr = Instant.ofEpochMilli(task.endTime)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
        .format(timeFormatter)

    // Считаем разницу в минутах для центральной плашки
    val durationMinutes =
        (task.endTime - task.startTime) / (60 * 1000L)

    val durationStr = if (durationMinutes >= 60) {
        val hours = durationMinutes / 60
        val minutes = durationMinutes % 60

        if (minutes > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${hours}h"
        }
    } else {
        "$durationMinutes Min"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Название задачи из базы
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    lineHeight = 32.sp
                ),
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            // Блок с иконками приоритета
            Row(
                horizontalArrangement = Arrangement.spacedBy((-16).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Иконка срочности (Ракета, если срочно, иначе Свуш/Не срочно)
                val urgentIconRes = if (task.isUrgent) R.drawable.rocket_transparent else R.drawable.swoosh_transparent
                PriorityBadgeIcon(iconRes = urgentIconRes)

                // 2. Иконка важности (Диамант, если важно, иначе Листик/Не важно)
                val importantIconRes = if (task.isImportant) R.drawable.diamond_transparent else R.drawable.leaf_transparent
                PriorityBadgeIcon(iconRes = importantIconRes)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Блок Start
            Column {
                Text(
                    text = startTimeStr,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Start",
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor
                )
            }

            // Центральная плашка с длительностью
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(contentColor)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = durationStr,
                    style = MaterialTheme.typography.labelMedium,
                    color = backgroundColor
                )
            }

            // Блок End
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = endTimeStr,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "End",
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor
                )
            }
        }
    }
}

// Вспомогательный мини-компонент для круглой иконки в шапке карточки
@Composable
fun PriorityBadgeIcon(
    iconRes: Int,
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                color = Color.White,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.6f)
                .padding(2.dp)
        )
    }
}