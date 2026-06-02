package net.paceapp.features.main.tabhost.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.features.main.analytics.navigation.analyticsScreen
import net.paceapp.features.main.history.navigation.historyScreen
import net.paceapp.features.main.home.navigation.homeScreen
import net.paceapp.features.main.profile.navigation.profileScreen
import net.paceapp.features.main.tabhost.models.BottomTab
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun TabHostContent(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnalyticsDetails: (AnalyticsMetricType) -> Unit,
    onNavigateToSetGait: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToManageWatch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToEventDetails: (String, String, String, String) -> Unit,
    onNavigateToDuplicateEvent: (String, String, String, String) -> Unit,
    ) {

    // The Local NavController for the 4 inner tabs
    val tabNavController = rememberNavController()
    val backgroundColor = AppColors.White
    val backdrop = rememberLayerBackdrop {
        drawRect(backgroundColor)
        drawContent()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = tabNavController,
            startDestination = BottomTab.Home.routeClass,
            modifier = Modifier.layerBackdrop(backdrop),

            // Define the animation when a new tab enters
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 300))
            },
            // Define the animation when the current tab leaves
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            },
            // (Optional) Define animations for when the user presses the back button
            popEnterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            //Home
            homeScreen(
                onBack = onBack,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToCreateEvent = onNavigateToCreateEvent,
                onNavigateToManageWatch = onNavigateToManageWatch,
                onNavigateToEventDetails = onNavigateToEventDetails,
            )

            //History
            historyScreen(
                onBack = onBack,
                onNavigateToEventDetails = onNavigateToEventDetails,
                onNavigateToDuplicateEvent = onNavigateToDuplicateEvent
            )

            //Analytics
            analyticsScreen(
                onBack = onBack,
                onNavigateToAnalyticsDetails = onNavigateToAnalyticsDetails
            )

            //Profile
            profileScreen(
                onBack = onBack,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToSetGait = onNavigateToSetGait,
                onNavigateToEditProfile = onNavigateToEditProfile,
                onNavigateToManageWatch = onNavigateToManageWatch,
            )
        }
        ///Bottom tab bar
        AppBottomBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.screenPadding)
                .navigationBarsPadding()
                .align(Alignment.BottomCenter),
            tabNavController = tabNavController,
            backdrop = backdrop
        )
    }
}
