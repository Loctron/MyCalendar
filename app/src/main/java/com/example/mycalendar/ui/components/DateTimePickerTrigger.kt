package com.example.mycalendar.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mycalendar.ui.theme.AppTypography
import com.example.mycalendar.ui.theme.TextBlack

@Composable
fun DateTimePickerTrigger(
    startDateTime: String,
    endDateTime: String,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Единый заголовок
        Text(
            text = "DATE & TIME",
            style = AppTypography.labelLarge,
            color = TextBlack,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Строка с выбором старта и конца
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // --- ЛЕВАЯ ЧАСТЬ: Время начала ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onStartClick() }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = startDateTime,
                    style = AppTypography.bodyLarge,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Start time",
                    tint = TextBlack,
                    modifier = Modifier.size(20.dp)
                )
            }

            // --- РАЗДЕЛИТЕЛЬ ---
            Text(
                text = "to",
                style = AppTypography.bodyLarge,
                color = Color.Gray
            )

            // --- ПРАВАЯ ЧАСТЬ: Время завершения ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onEndClick() }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = endDateTime,
                    style = AppTypography.bodyLarge,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "End time",
                    tint = TextBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}