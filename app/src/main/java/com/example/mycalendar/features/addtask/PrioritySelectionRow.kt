package com.example.mycalendar.features.addtask

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mycalendar.R
import com.example.mycalendar.ui.theme.AppTypography
import com.example.mycalendar.ui.theme.TextBlack

@Composable
fun PrioritySelectionRow(
    isUrgent: Boolean,
    isImportant: Boolean,
    onUpdatePriority: (Boolean, Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // --- Пара 1: Срочность ---
        PriorityIconButton(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.rocket_transparent,
            label = "Urgent",
            isSelected = isUrgent,
            onClick = { onUpdatePriority(true, isImportant) }
        )
        PriorityIconButton(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.swoosh_transparent,
            label = "Not urgent",
            isSelected = !isUrgent,
            onClick = { onUpdatePriority(false, isImportant) }
        )

        // --- Пара 2: Важность ---
        PriorityIconButton(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.diamond_transparent,
            label = "Important",
            isSelected = isImportant,
            onClick = { onUpdatePriority(isUrgent, true) }
        )
        PriorityIconButton(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.leaf_transparent,
            label = "No matter",
            isSelected = !isImportant,
            onClick = { onUpdatePriority(isUrgent, false) }
        )
    }
}

@Composable
fun PriorityIconButton(
    modifier: Modifier = Modifier,
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(CircleShape)
                .clickable { onClick() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = Color.Unspecified,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.4f)
                    // Полупрозрачность для неактивных иконок
                    .alpha(if (isSelected) 1f else 0.4f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = AppTypography.labelMedium,
            color = TextBlack,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            // Выделяем текст выбранной категории
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.alpha(if (isSelected) 1f else 0.5f)
        )
    }
}