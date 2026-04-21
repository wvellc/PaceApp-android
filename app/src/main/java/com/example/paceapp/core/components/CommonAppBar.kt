package com.example.paceapp.core.components

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.components.BaseAppBar

@Composable
fun CommonAppBar(
    title: String? = null,
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
    BaseAppBar(
        title = title,
        titleStyle = AppTheme.typography.size16.copy(
            fontWeight = FontWeight.Medium,
            color = AppColors.White,
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
        titleContent = {
            if (showAppLogo) {
                AppLogo(
                    modifier = Modifier,
                    logoStyle = LogoStyle.Horizontal,
                )
            }
        },
    )
}