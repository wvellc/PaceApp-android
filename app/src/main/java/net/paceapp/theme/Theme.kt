package net.paceapp.theme

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(

    primary = AppColors.NeonAquaBlue,
    secondary = AppColors.NeonAquaBlue,
    tertiary = AppColors.FluorescentMint,
    scrim = AppColors.NeonAquaBlue20,
    surfaceDim = AppColors.NeonAquaBlue20,


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun PaceAppTheme(
    content: @Composable () -> Unit
) {
    val paceTypography = PaceAppTypography()
    CompositionLocalProvider(
        LocalAppTypography provides paceTypography,
        LocalTextStyle provides TextStyle(fontFamily = paceTypography.fontFamily)
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            content = content
        )
    }
}

object AppTheme {
    val typography: PaceAppTypography
        @Composable
        get() = LocalAppTypography.current

    val bottomNavBarPadding: Dp
        @Composable
        get() = 70.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()


    val screenPadding: Dp = 16.dp

}