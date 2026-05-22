package net.paceapp.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import net.paceapp.R

// Set of Material typography styles to start with
@Immutable
data class PaceAppTypography(
    val fontFamily: FontFamily = GilroyFontFamily,
    // Weight-based Typography
    val light: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Light
    ),
    val regular: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal
    ),
    val medium: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium
    ),
    val semiBold: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold
    ),
    val bold: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold
    ),
    val extraBold: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.ExtraBold
    ),
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