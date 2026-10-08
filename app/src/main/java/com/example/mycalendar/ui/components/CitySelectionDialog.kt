package com.example.mycalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mycalendar.ui.theme.AppTypography
import com.example.mycalendar.ui.theme.BgGray
import com.example.mycalendar.ui.theme.CardYellow
import com.example.mycalendar.ui.theme.TextYellowDark
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectionDialog(
    onDismiss: () -> Unit,
    onCitySelected: (ZoneId, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Фиолетовый акцентный цвет для текстового поля
    val PurpleAccent = TextYellowDark

    val allGlobalCities = remember {
        ZoneId.getAvailableZoneIds()
            .filter { it.contains("/") }
            // Убираем технические системные зоны, чтобы список был чистым
            .filterNot { it.startsWith("SystemV") || it.startsWith("Etc") }
            .map { id ->
                val region = id.substringBefore("/")
                val city = id.substringAfterLast("/").replace("_", " ")
                Triple(city, region, ZoneId.of(id))
            }
            .distinctBy { it.first }
            .sortedBy { it.first }
    }

    val filteredCities = remember(searchQuery, allGlobalCities) {
        if (searchQuery.isBlank()) {
            allGlobalCities
        } else {
            allGlobalCities.filter {
                it.first.contains(searchQuery, ignoreCase = true) ||
                        it.second.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Используем базовый Dialog вместо AlertDialog для полного контроля над дизайном
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Позволяет сделать диалог шире
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Занимает 90% ширины экрана
                .fillMaxHeight(0.8f), // Ограничиваем высоту, чтобы не вылезал за края
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "Select City",
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextYellowDark,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Современное поле поиска
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Search any city...", style = AppTypography.bodyLarge, color = Color.Gray)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    textStyle = AppTypography.bodyLarge.copy(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextYellowDark,
                        unfocusedBorderColor = Color(0xFFE4E5E0),
                        cursorColor = TextYellowDark,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Список городов
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Занимает всё оставшееся пространство
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredCities) { (cityName, regionName, zoneId) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onCitySelected(zoneId, cityName)
                                    onDismiss()
                                }
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = cityName,
                                    style = AppTypography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = TextYellowDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = regionName,
                                    style = AppTypography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Кнопка отмены
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Cancel",
                        style = AppTypography.labelLarge,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}