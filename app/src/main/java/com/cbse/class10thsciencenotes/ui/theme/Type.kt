package com.cbse.class10thsciencenotes.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)


//var Typography= Typography(
//// Approximating 'Outfit' font with system fonts
//headlineLarge = TextStyle(
//fontWeight = FontWeight.Bold,
//fontSize = 33.sp,
//lineHeight = 37.sp,
//letterSpacing = 0.sp
//),
//bodyLarge = TextStyle(
//fontWeight = FontWeight.Normal,
//fontSize = 17.sp,
//lineHeight = 28.sp,
//letterSpacing = 0.5.sp
//),
//titleLarge = TextStyle(
//fontWeight = FontWeight.Bold,
//fontSize = 19.sp,
//letterSpacing = 0.5.sp
//)
//)