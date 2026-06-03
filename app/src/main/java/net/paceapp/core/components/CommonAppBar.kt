package net.paceapp.core.components

import android.app.Activity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.wvelabs.core_ui.components.BaseAppBar
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun CommonAppBar(
    title: String? = null,
    hasLightBackground: Boolean = false,
    titleColor: Color = if (hasLightBackground) AppColors.Black else AppColors.White,
    showBackButton: Boolean = true,
    backDispatcher: OnBackPressedDispatcher? = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
    onBackClick: () -> Unit = { backDispatcher?.onBackPressed() },
    isCollapsed: Boolean = true,
    collapsedHeight: Dp = 56.dp,
    expandedHeight: Dp = 56.dp,// If these are equal, it behaves as a normal App Bar
    background: @Composable BoxScope.() -> Unit = { },
    actions: @Composable RowScope.() -> Unit = {},
    showAppLogo: Boolean = false,
) {
    val view = LocalView.current
    val context = LocalContext.current

    if (!view.isInEditMode) {
        DisposableEffect(hasLightBackground) {
            val window = (context as? Activity)?.window
            if (window != null) {
                // Background is light, so DARK status bar
                // Background is dark,so LIGHT status bar
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    hasLightBackground
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                    hasLightBackground
            }
            onDispose { }
        }
    }

    val appLogo: @Composable () -> Unit = {
        AppLogo(
            modifier = Modifier,
            logoStyle = LogoStyle.Horizontal,
        )
    }
    BaseAppBar(
        title = title,
        titleStyle = AppTheme.typography.medium.copy(
            fontSize = 16.sp,
            color = titleColor,
        ),
        showBackButton = showBackButton,
        backButtonIcon = {
            Image(
                painter = painterResource(R.drawable.ic_back_arrow),
                contentDescription = stringResource(R.string.back),
                modifier = Modifier
                    .clip(CircleShape)
                    .defaultClickable(onClick = onBackClick)
            )
        },
        actionsArrangement = Arrangement.spacedBy(8.dp),
        isCollapsed = isCollapsed,
        collapsedHeight = collapsedHeight,
        expandedHeight = expandedHeight,
        background = background,
        actions = actions,
        titleContent = if (showAppLogo) appLogo else null
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2EB2FF)
@Composable
fun AppBarPreview() = CommonAppBar(title = "MY TITLE")