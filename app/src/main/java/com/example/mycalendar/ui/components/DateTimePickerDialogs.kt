package com.example.mycalendar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialogs(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onDateTimeSelected: (String) -> Unit
) {
    if (!isVisible) return

    // Внутреннее состояние: показываем ли мы сейчас часы
    var isTimePickerVisible by remember { mutableStateOf(false) }

    // Жестко сбрасываем состояние при каждом новом открытии шторки
    LaunchedEffect(isVisible) {
        if (isVisible) {
            isTimePickerVisible = false
        }
    }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    // --- КАЛЕНДАРЬ ---
    if (!isTimePickerVisible) {
        DatePickerDialog(
            onDismissRequest = {
                isTimePickerVisible = false
                onDismiss()
            },
            confirmButton = {
                TextButton(
                    onClick = { isTimePickerVisible = true },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text("OK", color = Color(0xFFA2A6A6))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isTimePickerVisible = false
                    onDismiss()
                }) {
                    Text("CANCEL", color = Color.LightGray)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color(0xFF3B4747)
            )
        ) {
            val pickerColors = DatePickerDefaults.colors(
                containerColor = Color(0xFF3B4747),
                titleContentColor = Color.White,
                headlineContentColor = Color.White,
                weekdayContentColor = Color.LightGray,
                dayContentColor = Color.White,
                todayDateBorderColor = Color(0xFFA2A6A6),
                todayContentColor = Color.White,
                selectedDayContainerColor = Color(0xFFA2A6A6),
                selectedDayContentColor = Color(0xFF3B4747),
                navigationContentColor = Color.White,
                yearContentColor = Color.White,
                currentYearContentColor = Color.White,
                selectedYearContainerColor = Color(0xFFA2A6A6),
                selectedYearContentColor = Color(0xFF3B4747)
            )

            DatePicker(
                state = datePickerState,
                colors = pickerColors,
                showModeToggle = false // Скрываем ручной ввод для избежания багов
            )
        }
    }
    // --- ЧАСЫ ---
    else {
        Dialog(
            onDismissRequest = {
                isTimePickerVisible = false
                onDismiss()
            }
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF3B4747)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select time",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    )

                    val timeColors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFF4A5555),
                        clockDialUnselectedContentColor = Color.White,
                        clockDialSelectedContentColor = Color(0xFF3B4747),
                        selectorColor = Color(0xFFA2A6A6),
                        timeSelectorSelectedContainerColor = Color(0xFFA2A6A6),
                        timeSelectorSelectedContentColor = Color(0xFF3B4747),
                        timeSelectorUnselectedContainerColor = Color(0xFF4A5555),
                        timeSelectorUnselectedContentColor = Color.White,
                        periodSelectorSelectedContainerColor = Color(0xFFA2A6A6),
                        periodSelectorSelectedContentColor = Color(0xFF3B4747),
                        periodSelectorUnselectedContainerColor = Color(0xFF3B4747),
                        periodSelectorUnselectedContentColor = Color.White,
                        periodSelectorBorderColor = Color(0xFF4A5555)
                    )

                    TimePicker(
                        state = timePickerState,
                        colors = timeColors
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            isTimePickerVisible = false // Возвращаемся к календарю
                        }) {
                            Text("BACK", color = Color.LightGray)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(onClick = {
                            val selectedMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            val selectedDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()

                            val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)

                            val formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a", Locale.ENGLISH)
                            val formattedStr = LocalDateTime.of(selectedDate, selectedTime).format(formatter)

                            onDateTimeSelected(formattedStr)

                            // Жестко сбрасываем состояния перед закрытием
                            isTimePickerVisible = false
                            onDismiss()
                        }) {
                            Text("OK", color = Color(0xFFA2A6A6))
                        }
                    }
                }
            }
        }
    }
}