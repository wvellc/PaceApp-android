package com.example.paceapp.features.main.tabhost.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.paceapp.features.main.tabhost.models.BottomTab
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.opacity
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousCapsule
import com.wvelabs.core_ui.components.liquidtabbar.LiquidBottomTabs
import com.wvelabs.core_ui.components.liquidtabbar.LiquidTabItem

@Composable
fun AppBottomBar(
    modifier: Modifier,
    tabNavController: NavHostController,
    backdrop: Backdrop,
) {

    val tabs = remember {
        listOf(
            BottomTab.Home,
            BottomTab.History,
            BottomTab.Analytics,
            BottomTab.Profile
        )
    }
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val selectedIndex = tabs.indexOfFirst { tab ->
        currentDestination?.hierarchy?.any { dest ->
            dest.route?.contains(tab.routeClass.simpleName ?: "") == true
        } == true
    }.let { if (it != -1) it else 0 }


    val tabShape = ContinuousCapsule

    LiquidBottomTabs(
        selectedTabIndex = { selectedIndex },
        onTabSelected = { index ->
            val tab = tabs[index]
            tabNavController.navigate(tab.route) {
                popUpTo(tabNavController.graph.id) {
                    inclusive = true
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        backdrop = backdrop,
        tabsCount = tabs.size,
        containerShape = tabShape,

        // --- Styling to match your screenshot ---
        containerHeight = 58.dp,
        padding = 8.dp,
        // Colors from screenshot
        containerColor = AppColors.White.copy(0.3f),
        selectedTabColor = AppColors.NeonAquaBlue,
        unselectedTabColor = AppColors.FashionGray,
        // Disable backdrop effects if you want solid colors like the screenshot
        containerEffects = {
            opacity(0.95f)
            vibrancy()
            blur(20f.dp.toPx())
            lens(
                refractionHeight = 16f.dp.toPx(),
                refractionAmount = 32f.dp.toPx(),
            )
        },
        tabsEffects = { progress ->
            vibrancy()
            blur(20f.dp.toPx())
            lens(
                refractionHeight = 16f.dp.toPx() * progress,
                refractionAmount = 32f.dp.toPx() * progress,
            )
        },
        indicatorEffects = { progress ->
            lens(
                refractionHeight = 10f.dp.toPx() * progress,
                refractionAmount = 14f.dp.toPx() * progress,
                chromaticAberration = true
            )
        },
        modifier = modifier,
        borderStroke = BorderStroke(
            1.dp,
            Brush.verticalGradient(AppColors.bottomTabBorderGradient)
        )
    ) { index, measurementModifier, providedColor, isBaseLayer ->
        val tab = tabs[index]
        val isSelected = selectedIndex == index

        val finalIconColor = if (isBaseLayer) {
            // If it's the base layer AND selected, it sits on the Neon pill, so use White.
            // Otherwise, it is an unselected tab, so use the provided DarkCharcoal!
            if (isSelected) AppColors.White else providedColor
        } else {
            // If it is the active/invisible layer, it always uses the provided NeonAquaBlue.
            providedColor
        }
        // The LiquidBottomTabs handles the clicks/dragging automatically.
        // We just define what the item looks like!
        LiquidTabItem(
            modifier = measurementModifier,
            shape = tabShape,
            selectedColor = if (isBaseLayer) AppColors.NeonAquaBlue else AppColors.Transparent,
            isSelected = isSelected,
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 64.dp)
                        .padding(horizontal = 12.dp)
                        .animateContentSize()
                ) {
                    // Crossfade icon resource
                    Crossfade(
                        targetState = if (isSelected) tab.selectedIconResId else tab.iconResId,
                        animationSpec = tween(300),
                        label = "icon_fade"
                    ) { iconRes ->
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = stringResource(id = tab.titleResId),
                            modifier = Modifier
                                .size(28.dp),
                            colorFilter = ColorFilter.tint(finalIconColor)
                        )
                    }

                    // Smoothly expand and reveal the text
                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn(tween(250)) + expandHorizontally(
                            animationSpec = tween(300),
                            expandFrom = Alignment.Start
                        ),
                        exit = fadeOut(tween(200)) + shrinkHorizontally(
                            animationSpec = tween(300),
                            shrinkTowards = Alignment.Start
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = tab.titleResId),
                                style = AppTheme.typography.semiBold.copy(
                                    fontSize = 12.sp,
                                    color = finalIconColor
                                ),
                                maxLines = 1,
                                softWrap = false // Prevents text from jumping to a second line while shrinking!
                            )
                        }
                    }
                }
            },
        )
    }
}
