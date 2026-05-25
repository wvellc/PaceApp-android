package net.paceapp.features.main.tabhost.components

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.opacity
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousCapsule
import com.wvelabs.core_ui.components.liquidtabbar.LiquidBottomTabs
import com.wvelabs.core_ui.components.liquidtabbar.LiquidTabItem
import net.paceapp.features.main.tabhost.models.BottomTab
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

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
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(selectedIndex) {
        val tab = tabs[selectedIndex]
        tabNavController.navigate(tab.route) {
            popUpTo(tabNavController.graph.id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val tabShape = ContinuousCapsule

    LiquidBottomTabs(
        selectedTabIndex = { selectedIndex },
        onTabSelected = { index ->
            if (selectedIndex != index) {
                selectedIndex = index
            }
        },
        backdrop = backdrop,
        tabsCount = tabs.size,
        containerShape = tabShape,
        containerHeight = 58.dp,
        padding = 8.dp,
        containerColor = AppColors.White.copy(0.3f),
        selectedTabColor = AppColors.NeonAquaBlue,
        unselectedTabColor = AppColors.FashionGray,
        selectedContentColor = AppColors.White,
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
    ) { index, measurementModifier, contentColor, indicatorColor, isVisuallySelected ->
        val tab = tabs[index]

        // The LiquidBottomTabs handles the clicks/dragging automatically.
        // We just define what the item looks like!
        LiquidTabItem(
            modifier = measurementModifier,
            shape = tabShape,
            selectedColor = indicatorColor,
            isSelected = isVisuallySelected,
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
                        targetState = if (isVisuallySelected) tab.selectedIconResId else tab.iconResId,
                        animationSpec = tween(300),
                        label = "icon_fade"
                    ) { iconRes ->
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = stringResource(id = tab.titleResId),
                            modifier = Modifier
                                .size(28.dp),
                            colorFilter = ColorFilter.tint(contentColor)
                        )
                    }

                    // Smoothly expand and reveal the text
                    AnimatedVisibility(
                        visible = isVisuallySelected,
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
                                    color = contentColor
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
