package com.example.mycalendar.features.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthSelector(
    selectedMonth: YearMonth,
    onMonthSelected: (YearMonth) -> Unit
) {
    // Генерируем 12 месяцев текущего года
    val months = remember(selectedMonth.year) {
        (1..12).map { YearMonth.of(selectedMonth.year, it) }
    }

    // Автоматический скролл к текущему месяцу при запуске
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = maxOf(0, selectedMonth.monthValue - 2))

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(months) { month ->
            val isSelected = month == selectedMonth
            val monthName = month.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase()

            Text(
                text = monthName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.Black else Color.LightGray,
                modifier = Modifier.clickable { onMonthSelected(month) }
            )
        }
    }
}