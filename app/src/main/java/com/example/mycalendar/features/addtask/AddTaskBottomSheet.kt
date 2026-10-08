package com.example.mycalendar.features.addtask

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycalendar.ui.components.DateTimePickerDialogs
import com.example.mycalendar.ui.components.DateTimePickerTrigger
import com.example.mycalendar.ui.theme.AppTypography
import com.example.mycalendar.ui.theme.BgGray
import com.example.mycalendar.ui.theme.CardBlueGray
import com.example.mycalendar.ui.theme.PillBlueGray
import com.example.mycalendar.ui.theme.TextBlack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Лаунчер для запроса прав
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.toggleReminder(true)
            }
        }
    )

    val state by viewModel.uiState.collectAsState()

    // Хранит "START", "END" или null (если закрыт)
    var activePicker by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scrollState = rememberScrollState()

    val maxTitleLength = 40
    val maxDescLength = 140

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BgGray,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        // Оборачиваем Column в Box, чтобы клик по пустой области работал железобетонно
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f) // Занимаем максимум доступной высоты шторки для клика
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    })
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(start = 24.dp, end = 24.dp, bottom = 48.dp)
            ) {
                Text(
                    text = "NEW TASK",
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = state.title,
                    onValueChange = {
                        if (it.length <= maxTitleLength) viewModel.updateTitle(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(text = "Task Name", style = AppTypography.bodyLarge, color = Color.Gray)
                    },
                    textStyle = AppTypography.bodyLarge,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    // Явно снимаем фокус при нажатии галочки в поле Названия
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PillBlueGray,
                        unfocusedBorderColor = PillBlueGray,
                        cursorColor = Color(0xFF999488),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = PillBlueGray,
                        focusedTextColor = TextBlack,
                        unfocusedTextColor = Color.White,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.description,
                    onValueChange = { newText ->
                        if (newText.contains("\n")) {
                            val cleanText = newText.replace("\n", "")
                            if (cleanText.length <= maxDescLength) {
                                viewModel.updateDescription(cleanText)
                            }
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        } else if (newText.length <= maxDescLength) {
                            viewModel.updateDescription(newText)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(text = "Description", style = AppTypography.bodyLarge, color = Color.Gray)
                    },
                    textStyle = AppTypography.bodyLarge,
                    singleLine = false,
                    // Возвращаем ImeAction.Done
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    // НОВОЕ: Обрабатываем onDone для многострочного поля
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PillBlueGray,
                        unfocusedBorderColor = PillBlueGray,
                        cursorColor = Color(0xFF999488),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = PillBlueGray,
                        focusedTextColor = TextBlack,
                        unfocusedTextColor = Color.White,
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Размещаем старт и конец в одну линию
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    DateTimePickerTrigger(
                        startDateTime = state.startDateTimeStr,
                        endDateTime = state.endDateTimeStr,
                        onStartClick = { activePicker = "START" },
                        onEndClick = { activePicker = "END" }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "PRIORITY",
                    style = AppTypography.labelLarge,
                    color = TextBlack,
                )

                PrioritySelectionRow(
                    isUrgent = state.isUrgent,
                    isImportant = state.isImportant,
                    onUpdatePriority = { urgent, important ->
                        viewModel.updatePriority(isUrgent = urgent, isImportant = important)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REMINDERS",
                        style = AppTypography.labelLarge,
                        color = TextBlack
                    )

                    Switch(
                        checked = state.isReminderEnabled,
                        onCheckedChange = { isChecked ->
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                // Если Android 13+ и права еще не выданы -> запрашиваем
                                if (ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.POST_NOTIFICATIONS)
                                    != PackageManager.PERMISSION_GRANTED) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.toggleReminder(true)
                                }
                            } else {
                                viewModel.toggleReminder(isChecked)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TextBlack,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = CardBlueGray,
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Если это редактирование, показываем кнопку удаления
                if (state.isEditing) {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            viewModel.deleteTask(onSuccess = {
                                onDismiss()
                            })
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "DELETE TASK",
                            style = AppTypography.labelLarge,
                            color = Color.Red
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Кнопка сохранения (меняет текст автоматически)
                Button(
                    onClick = {
                        viewModel.saveTask(onSuccess = {
                            onDismiss()
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PillBlueGray
                    )
                ) {
                    Text(
                        text = if (state.isEditing) "SAVE CHANGES" else "ADD TASK",
                        style = AppTypography.bodyLarge,
                        color = Color.White,
                    )
                }
            }
        }
    }
    DateTimePickerDialogs(
        isVisible = activePicker != null,
        onDismiss = { activePicker = null },
        onDateTimeSelected = { formattedStr ->
            // Отправляем данные в зависимости от того, какой пикер был открыт
            if (activePicker == "START") {
                viewModel.updateStartDateTime(formattedStr)
            } else if (activePicker == "END") {
                viewModel.updateEndDateTime(formattedStr)
            }
        }
    )
}