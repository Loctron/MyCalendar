package com.example.mycalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycalendar.features.addtask.AddTaskBottomSheet
import com.example.mycalendar.features.calendar.CalenderScreen
import com.example.mycalendar.features.dashboard.CalendarDashboardScreen
import com.example.mycalendar.ui.theme.MyCalenderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Обязательно для Hilt, чтобы работала ViewModel внутри шторки
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCalenderTheme {
                Surface {
                    MainAppScreen()
                }
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    // Состояние навигации
    var currentTab by remember { mutableStateOf("Today") }
    // Состояние шторки
    var showAddSheet by remember { mutableStateOf(false) }

    // Развилка: показываем нужный экран в зависимости от вкладки
    when (currentTab) {
        "Today" -> {
            CalendarDashboardScreen(
                currentTab = currentTab,
                onTabChange = { currentTab = it },
                onAddClick = { showAddSheet = true },
                viewModel = hiltViewModel()
            )
        }
        "Calender" -> {
            CalenderScreen(
                currentTab = currentTab,
                onTabChange = { currentTab = it },
                onAddClick = { showAddSheet = true }
            )
        }
    }

    // Шторка рисуется поверх любого из выбранных экранов
    if (showAddSheet) {
        AddTaskBottomSheet(
            onDismiss = { showAddSheet = false }
        )
    }
}