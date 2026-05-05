package com.example.paceapp.features.main.tabhost.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.paceapp.core.components.liquidbottomtabs.LiquidBottomTab
import com.example.paceapp.core.components.liquidbottomtabs.LiquidBottomTabs
import com.example.paceapp.features.main.home.navigation.homeScreen
import com.example.paceapp.features.main.tabhost.TabHostContract.Event
import com.example.paceapp.features.main.tabhost.TabHostContract.State
import com.example.paceapp.features.main.tabhost.domain.BottomTab
import com.example.paceapp.theme.AppColors
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
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
    Box(modifier = Modifier.fillMaxSize()) {

        // 2. The Content draws FIRST (so it sits behind the glass)
        NavHost(
            navController = tabNavController,
            startDestination = BottomTab.Home.routeClass,
            modifier = Modifier.layerBackdrop(backdrop)
        ) {
            homeScreen(onBack = { onEvent(Event.OnBackClick) })
            // historyScreen(...)
        }
        var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

        LiquidBottomTabs(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .safeContentPadding()
                .border(
                    BorderStroke(1.dp, Brush.verticalGradient(AppColors.bottomTabBorderGradient)),
                    shape = tabShape
                ),
            selectedTabIndex = { selectedTabIndex },
            onTabSelected = { selectedTabIndex = it },
            backdrop = backdrop,
            tabsCount = 4,
        ) {
            repeat(4) { index ->
                LiquidBottomTab({ selectedTabIndex = index }) {
                    BasicText(
                        "Tab ${index + 1}",
                        style = TextStyle(backgroundColor, 12f.sp)
                    )
                }
            }
        }

//        Box(
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//                .safeContentPadding()
//
//                .drawBackdrop(
//                    backdrop = backdrop,
//                    shape = { tabShape },
//                    effects = {
//                        vibrancy()
//                        blur(4f.dp.toPx())
//                        lens(16f.dp.toPx(), 32f.dp.toPx())
//                    },
//                    onDrawSurface = { drawRect(AppColors.White.copy(alpha = 0.2f)) }
//                )
//                .height(58f.dp)
//                .fillMaxWidth()
//                .border(
//                    BorderStroke(1.dp, Brush.verticalGradient(AppColors.bottomTabBorderGradient)),
//                    shape = tabShape
//                )
//                .align(Alignment.BottomCenter)) {
////            AppBottomBar(tabNavController)
//        }
    }
}
