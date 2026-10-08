package com.example.mycalendar.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mycalendar.ui.components.CitySelectionDialog
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DateAndTimeRow(
    topZoneId: ZoneId,
    bottomZoneId: ZoneId,
    onTopZoneChange: (ZoneId) -> Unit,
    onBottomZoneChange: (ZoneId) -> Unit
) {
    // --- Левая часть: Даты ---
    val today = remember { LocalDate.now() }
    val dayOfWeek = remember { today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH) }
    val dayOfMonth = remember { today.dayOfMonth.toString() }
    val monthOfYear = remember { today.monthValue.toString() }
    val month = remember { today.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase() }

    // --- Правая часть: Динамическое время ---
    // Состояние для живого тиканья часов
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Эффект, который обновляет время каждую секунду
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = System.currentTimeMillis()
        }
    }

    // Форматировщик
    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH) }

    // Вычисляем время для обеих зон
    val topTime = remember(currentTime, topZoneId) { ZonedDateTime.now(topZoneId).format(timeFormatter) }
    val bottomTime = remember(currentTime, bottomZoneId) { ZonedDateTime.now(bottomZoneId).format(timeFormatter) }

    // Извлекаем названия городов из ZoneId
    var topCityName by remember(topZoneId) { mutableStateOf(topZoneId.id.substringAfterLast("/").replace("_", " ")) }
    var bottomCityName by remember(bottomZoneId) { mutableStateOf(bottomZoneId.id.substringAfterLast("/").replace("_", " ")) }

    // Состояния для показа всплывающих окон
    var showTopDialog by remember { mutableStateOf(false) }
    var showBottomDialog by remember { mutableStateOf(false) }

    // --- 3. Верстка ---
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Левый блок с датой
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = dayOfWeek, color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$dayOfMonth.$monthOfYear",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
            )
            Text(
                text = month,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
            )
        }

        // Разделитель
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(80.dp)
                .background(Color.DarkGray.copy(alpha = 0.4f))
        )

        // Правый блок со временем (теперь кликабельный)
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TimezoneItem(
                time = topTime,
                city = if (topZoneId == ZoneId.systemDefault()) "Local Time" else topCityName,
                modifier = Modifier.clickable { showTopDialog = true } // Открываем диалог при клике
            )
            TimezoneItem(
                time = bottomTime,
                city = bottomCityName,
                modifier = Modifier.clickable { showBottomDialog = true } // Открываем диалог при клике
            )
        }
    }

    // --- Диалоги выбора города ---
    if (showTopDialog) {
        CitySelectionDialog(
            onDismiss = { showTopDialog = false },
            onCitySelected = { zoneId, cityName ->
                onTopZoneChange(zoneId)
                topCityName = cityName
            }
        )
    }

    if (showBottomDialog) {
        CitySelectionDialog(
            onDismiss = { showBottomDialog = false },
            onCitySelected = { zoneId, cityName ->
                onBottomZoneChange(zoneId)
                bottomCityName = cityName
            }
        )
    }
}

@Composable
fun TimezoneItem(time: String, city: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        Text(
            text = city,
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )
    }
}