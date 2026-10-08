package com.example.mycalendar.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mycalendar.R


val HelveticaNeue = FontFamily(
    // 100 - Thin
    Font(R.font.helvetica_neue_thin, FontWeight.Thin),
    Font(R.font.helvetica_neue_thin_italic, FontWeight.Thin, FontStyle.Italic),

    // 200 - ExtraLight / UltraLight
    Font(R.font.helvetica_neue_ultra_light, FontWeight.ExtraLight),
    Font(R.font.helvetica_neue_ultra_light_italic, FontWeight.ExtraLight, FontStyle.Italic),

    // 300 - Light
    Font(R.font.helvetica_neue_light, FontWeight.Light),
    Font(R.font.helvetica_neue_light_italic, FontWeight.Light, FontStyle.Italic),

    // 400 - Normal
    Font(R.font.helvetica_neue_roman, FontWeight.Normal),
    Font(R.font.helvetica_neue_italic, FontWeight.Normal, FontStyle.Italic),

    // 500 - Medium
    Font(R.font.helvetica_neue_medium, FontWeight.Medium),
    Font(R.font.helvetica_neue_medium_italic, FontWeight.Medium, FontStyle.Italic),

    // 700 - Bold
    Font(R.font.helvetica_neue_bold, FontWeight.Bold),
    Font(R.font.helvetica_neue_bold_italic, FontWeight.Bold, FontStyle.Italic),

    // 800 - ExtraBold / Heavy
    Font(R.font.helvetica_neue_heavy, FontWeight.ExtraBold),
    Font(R.font.helvetica_neue_heavy_italic, FontWeight.ExtraBold, FontStyle.Italic),

    // 900 - Black
    Font(R.font.helvetica_neue_black, FontWeight.Black),
    Font(R.font.helvetica_neue_black_italic, FontWeight.Black, FontStyle.Italic)
)

// Настраиваем стандартные стили
val AppTypography = Typography(
    // Для самых крупных элементов
    displayMedium = TextStyle(
        fontFamily = HelveticaNeue,
        fontWeight = FontWeight.Medium,
        fontSize = 48.sp,
    ),
    // Для заголовков
    titleLarge = TextStyle(
        fontFamily = HelveticaNeue,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
    ),
    // Стандартный текст
    bodyLarge = TextStyle(
        fontFamily = HelveticaNeue,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    // Мелкий текст
    labelMedium = TextStyle(
        fontFamily = HelveticaNeue,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    )
)