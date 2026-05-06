package com.example.paceapp.features.main.tabhost.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.paceapp.features.main.analytics.navigation.analyticsScreen
import com.example.paceapp.features.main.history.navigation.historyScreen
import com.example.paceapp.features.main.home.navigation.homeScreen
import com.example.paceapp.features.main.profile.navigation.profileScreen
import com.example.paceapp.features.main.tabhost.TabHostContract.Event
import com.example.paceapp.features.main.tabhost.TabHostContract.State
import com.example.paceapp.features.main.tabhost.domain.BottomTab
import com.example.paceapp.theme.AppColors
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
internal fun TabHostContent(
    state: State,
    onEvent: (Event) -> Unit
) {

    // The Local NavController for the 4 inner tabs
    val tabNavController = rememberNavController()
    val backgroundColor = AppColors.White
    val backdrop = rememberLayerBackdrop {
        drawRect(backgroundColor)
        drawContent()
    }
    val tabShape = ContinuousRoundedRectangle(96.dp)
    val durationMillis = 400
    val defaultEasing = FastOutSlowInEasing
    Box(modifier = Modifier.fillMaxSize()) {

        // 2. The Content draws FIRST (so it sits behind the glass)
        NavHost(
            navController = tabNavController,
            startDestination = BottomTab.Home.routeClass,
            modifier = Modifier.layerBackdrop(backdrop),
        ) {
            homeScreen(onBack = { onEvent(Event.OnBackClick) })
            historyScreen(onBack = { onEvent(Event.OnBackClick) })
            analyticsScreen(onBack = { onEvent(Event.OnBackClick) })
            profileScreen(onBack = { onEvent(Event.OnBackClick) })
        }

        AppBottomBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .safeContentPadding()
                .align(Alignment.BottomCenter),
            tabNavController = tabNavController,
            backdrop = backdrop
        )


        /*Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .safeContentPadding()
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { tabShape },
                    effects = {
                        vibrancy()
                        blur(4f.dp.toPx())
                        lens(16f.dp.toPx(), 32f.dp.toPx())
                    },
                    onDrawSurface = { drawRect(AppColors.White.copy(alpha = 0.2f)) }
                )
                .height(58f.dp)
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, Brush.verticalGradient(AppColors.bottomTabBorderGradient)),
                    shape = tabShape
                )
                .align(Alignment.BottomCenter)) {
            AppBottomBar(
                modifier = Modifier.padding(all = 8.dp),
                tabNavController = tabNavController,
                backdrop = backdrop
            )
        }*/
    }
}
