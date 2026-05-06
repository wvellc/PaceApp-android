package com.example.paceapp.features.main.tabhost.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.paceapp.core.components.liquidtabbar.LiquidBottomTabs
import com.example.paceapp.core.components.liquidtabbar.LiquidTabItem
import com.example.paceapp.features.main.tabhost.domain.BottomTab
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousCapsule
import com.wvelabs.core_ui.extensions.defaultAnimSpec

@Composable
fun AppBottomBar(
    modifier: Modifier,
    tabNavController: NavHostController,
    backdrop: Backdrop,
) {
    val tabs = listOf(BottomTab.Home, BottomTab.History, BottomTab.Analytics, BottomTab.Profile)
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val selectedIndex = remember(currentDestination) {
        tabs.indexOfFirst { currentDestination?.hasRoute(it.routeClass) == true }.coerceAtLeast(0)
    }


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
        containerColor = AppColors.White.copy(alpha = 0.3f),
        // Disable backdrop effects if you want solid colors like the screenshot
        containerEffects = {
            vibrancy()
            blur(4f.dp.toPx())
            lens(16f.dp.toPx(), 32f.dp.toPx())
        },
        tabsEffects = { progress ->
            vibrancy()
            blur(4f.dp.toPx())
            lens(
                16f.dp.toPx() * progress,
                32f.dp.toPx() * progress
            )
        },
        indicatorEffects = { progress ->
            lens(
                10f.dp.toPx() * progress,
                14f.dp.toPx() * progress,
                chromaticAberration = true
            )
        },
        modifier = modifier
    ) {
        // 4. Render the Tab Items
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedIndex == index

            // The LiquidBottomTabs handles the clicks/dragging automatically.
            // We just define what the item looks like!
            LiquidTabItem(
                modifier = Modifier.width(0.dp),
                shape = tabShape,
                isSelected = isSelected,
                selectedColor = AppColors.NeonAquaBlue,
            ) {
                Crossfade(
                    targetState = isSelected,
                    animationSpec = defaultAnimSpec(300),
                ) { selected ->
                    if (selected) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(tab.selectedIconResId),
                                contentDescription = stringResource(id = tab.titleResId),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = tab.titleResId),
                                style = AppTheme.typography.size12.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.White
                                )
                            )
                        }
                    } else {
                        Image(
                            painter = painterResource(tab.iconResId),
                            contentDescription = stringResource(id = tab.titleResId),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }

    /*NavigationBar(
        modifier = modifier,
        containerColor = AppColors.White.copy(alpha = 0.1f),
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedIndex == index
            LiquidTabItem(
                modifier = Modifier.width(0.dp),
                shape = tabShape,
                isSelected = isSelected,
                selectedColor = AppColors.NeonAquaBlue,
                onClick = {
                    val tab = tabs[index]
                    tabNavController.navigate(tab.route) {
                        popUpTo(tabNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(
                            id = when {
                                isSelected -> tab.selectedIconResId
                                else -> tab.iconResId
                            }
                        ),
                        contentDescription = stringResource(id = tab.titleResId),
                        modifier = Modifier.size(28.dp)
                    )

                    // Show text only when selected (matching your screenshot)
                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn(tween(300)) ,
                        exit = fadeOut(tween(150))
                    ) {
                        Row {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = tab.titleResId),
                                style = AppTheme.typography.size12.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }*/

}
