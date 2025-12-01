package com.example.playlistmaker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium))
val YsDisplayRegular = FontFamily(Font(R.font.ys_display_regular))

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight(700),
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight(500),
        fontSize = 19.sp
    ),
    titleSmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight(400),
        fontSize = 13.sp
    ),
    bodyLarge = TextStyle(
        textAlign = TextAlign.Start,
        fontSize = 16.sp,
        fontWeight = FontWeight(400),
        fontFamily = YsDisplayRegular
    ),
    bodyMedium = TextStyle(
        textAlign = TextAlign.Start,
        fontSize = 24.sp,
        fontFamily = YsDisplayMedium
    ),
    bodySmall = TextStyle(
        textAlign = TextAlign.Start,
        fontSize = 16.sp,
        fontWeight = FontWeight(400),
        fontFamily = YsDisplayRegular
    ),
    labelLarge = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 13.sp
    ),
    labelSmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight(400),
        fontSize = 11.sp
    )
)

val PlaylistInfoStyle = TextStyle(
    textAlign = TextAlign.Start,
    fontSize = 12.sp,
    fontWeight = FontWeight(400),
    fontFamily = YsDisplayRegular,
    letterSpacing = 0.4.sp
)
