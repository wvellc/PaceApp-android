package com.example.paceapp.ui.theme

import androidx.compose.material3.Typography
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
    val size34: TextStyle = TextStyle(fontSize = 34.sp, fontFamily = fontFamily),

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