package com.example.paceapp.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.paceapp.R

// Set of Material typography styles to start with
@Immutable
data class PaceAppTypography(
    val fontFamily: FontFamily = GilroyFontFamily, // Set your custom font here
    val size10: TextStyle = TextStyle(fontSize = 10.sp, fontFamily = fontFamily),
    val size12: TextStyle = TextStyle(fontSize = 12.sp, fontFamily = fontFamily),
    val size14: TextStyle = TextStyle(fontSize = 14.sp, fontFamily = fontFamily),
    val size16: TextStyle = TextStyle(fontSize = 16.sp, fontFamily = fontFamily),
    val size18: TextStyle = TextStyle(fontSize = 18.sp, fontFamily = fontFamily),
    val size20: TextStyle = TextStyle(fontSize = 20.sp, fontFamily = fontFamily),
    val size22: TextStyle = TextStyle(fontSize = 22.sp, fontFamily = fontFamily),
    val size24: TextStyle = TextStyle(fontSize = 24.sp, fontFamily = fontFamily),
    val size26: TextStyle = TextStyle(fontSize = 26.sp, fontFamily = fontFamily),
    val size28: TextStyle = TextStyle(fontSize = 28.sp, fontFamily = fontFamily),
    val size30: TextStyle = TextStyle(fontSize = 30.sp, fontFamily = fontFamily),
    val size32: TextStyle = TextStyle(fontSize = 32.sp, fontFamily = fontFamily),
    val size34: TextStyle = TextStyle(fontSize = 34.sp, fontFamily = fontFamily),
    val size36: TextStyle = TextStyle(fontSize = 36.sp, fontFamily = fontFamily),
    val size46: TextStyle = TextStyle(fontSize = 46.sp, fontFamily = fontFamily),
    val size56: TextStyle = TextStyle(fontSize = 56.sp, fontFamily = fontFamily)
)

val LocalAppTypography = staticCompositionLocalOf { PaceAppTypography() }
val GilroyFontFamily = FontFamily(
    Font(R.font.gilroy_light, FontWeight.Light),
    Font(R.font.gilroy_regular, FontWeight.Normal),
    Font(R.font.gilroy_medium, FontWeight.Medium),
    Font(R.font.gilroy_semi_bold, FontWeight.SemiBold),
    Font(R.font.gilroy_bold, FontWeight.Bold),
    Font(R.font.gilroy_extra_bold, FontWeight.ExtraBold),
)