package net.paceapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.wvelabs.core_ui.utils.LocalAppBackdrop
import com.wvelabs.core_ui.utils.rememberGlobalExitHandler
import net.paceapp.R
import net.paceapp.features.authentication.buildprofile.navigation.buildProfileScreen
import net.paceapp.features.authentication.login.navigation.loginScreen
import net.paceapp.features.authentication.profilecreated.navigation.profileCreatedScreen
import net.paceapp.features.authentication.verifyotp.navigation.verifyOtpScreen
import net.paceapp.features.common.webview.navigation.webviewScreen
import net.paceapp.features.main.analyticsdetail.navigation.analyticsDetailScreen
import net.paceapp.features.main.createevent.navigation.createEventScreen
import net.paceapp.features.main.duplicateevent.navigation.duplicateEventScreen
import net.paceapp.features.main.editevent.navigation.editEventScreen
import net.paceapp.features.main.editprofile.navigation.editprofileScreen
import net.paceapp.features.main.eventdetails.navigation.eventDetailsScreen
import net.paceapp.features.main.eventmap.navigation.eventMapScreen
import net.paceapp.features.main.favoriteactivities.navigation.favoriteActivitiesScreen
import net.paceapp.features.main.managewatch.navigation.managewatchScreen
import net.paceapp.features.main.notifications.navigation.notificationsScreen
import net.paceapp.features.main.settings.navigation.settingsScreen
import net.paceapp.features.main.tabhost.navigation.tabHostScreen
import net.paceapp.features.main.updategait.navigation.updateGaitScreen
import net.paceapp.features.splash.navigation.SplashRoute
import net.paceapp.features.splash.navigation.splashScreen
import net.paceapp.core.auth.AuthManager
import net.paceapp.session.AppSessionManager

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Any = SplashRoute,
    sessionManager: AppSessionManager,
    authManager: AuthManager,
) {
    //Nav action
    val triggerExit =
        rememberGlobalExitHandler(message = stringResource(R.string.back_press_to_exit_message))
    val navActions = remember(navController) {
        AppNavActions(navController, onRootExit = triggerExit)
    }
    // Navigation transition animation and duration
    val durationMillis = 400
    val defaultEasing = FastOutSlowInEasing
    // Listen for the global session expired event
    LaunchedEffect(Unit) {
        sessionManager.sessionExpiredEvent.collect {
            navActions.toLogin()
        }
    }
    // Email-link sign-in completes out of band (MainActivity) — reset to Splash so it
    // re-evaluates the now-active session and routes to TabHost/BuildProfile.
    LaunchedEffect(Unit) {
        authManager.signInCompleted.collect {
            navController.navigate(SplashRoute) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    val backdrop = rememberLayerBackdrop()
    CompositionLocalProvider(LocalAppBackdrop provides backdrop) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.layerBackdrop(backdrop),
            // PUSH FORWARD: New screen slides in fully from the right
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis, easing = defaultEasing)
                )
            },

            // PUSH FORWARD: Old screen slides left (parallax) AND fades out (dimming effect)
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis, easing = defaultEasing),
                    targetOffset = { it / 3 } // 33% depth is smoother than 50%
                ) + fadeOut(
                    targetAlpha = 0.5f,
                    animationSpec = tween(durationMillis, easing = defaultEasing)
                )
            },

            // POP BACK: Old screen slides in from left (parallax) AND fades back in
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis, easing = defaultEasing),
                    initialOffset = { it / 3 }
                ) + fadeIn(
                    initialAlpha = 0.5f,
                    animationSpec = tween(durationMillis, easing = defaultEasing)
                )
            },

            // POP BACK: Current screen slides fully off to the right
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis, easing = defaultEasing)
                )
            }
        ) {
            //Splash screen
            splashScreen(
                onNavigateToLogin = navActions::toLogin,
                onNavigateToTabHost = navActions::toTabHost,
                onNavigateToBuildProfile = navActions::toBuildProfile
            )

            //Login screen
            loginScreen(
                onBack = navActions::goBack,
                onNavigateToWebview = navActions::toWebView,
                onNavigateToVerifyOtp = navActions::toVerifyOtp,
            )

            //Webview
            webviewScreen()

            //Verify Otp
            verifyOtpScreen(
                onBack = navActions::goBack,
                onNavigateToBuildProfile = navActions::toBuildProfile,
                onNavigateToTabHost = navActions::toTabHost,
            )

            //Otp success
            /** Feature removed
            otpSuccessScreen(
            onBack = navActions::goBack,
            onNavigateToBuildProfile = navActions::toBuildProfile,
            onNavigateToTabHost = navActions::toTabHost,
            )*/

            //Build profile
            buildProfileScreen(
                onBack = navActions::goBack,
                onNavigateToProfileCreated = navActions::toProfileCreated
            )
            //Profile creation success
            profileCreatedScreen(
                onBack = navActions::goBack,
                onNavigateToTabHost = navActions::toTabHost
            )

            // The Tab Host (Post-Login Dashboard)
            tabHostScreen(
                // Pass global actions down to the TabHost
                onBack = navActions::goBack,
                onNavigateToSettings = navActions::toSettings,
                onNavigateToAnalyticsDetails = navActions::toAnalyticsDetails,
                onNavigateToSetGait = navActions::toUpdateGait,
                onNavigateToEditProfile = navActions::toEditProfile,
                onNavigateToManageWatch = navActions::toManageWatch,
                onNavigateToNotifications = navActions::toNotifications,
                onNavigateToCreateEvent = navActions::toCreateEvent,
                onNavigateToEventDetails = navActions::toEventDetails,
                onNavigateToDuplicateEvent = navActions::toDuplicateEvent,
                onNavigateToFavorites = navActions::toFavorites,
            )

            //Settings
            settingsScreen(
                onBack = navActions::goBack,
                onNavigateToWebview = navActions::toWebView
            )

            //Analytics Details
            analyticsDetailScreen(onBack = navActions::goBack)

            //Update Gait
            updateGaitScreen(onBack = navActions::goBack)

            //Edit Profile
            editprofileScreen(onBack = navActions::goBack)

            //Manage Watch
            managewatchScreen(onBack = navActions::goBack)

            //Notifications
            notificationsScreen(onBack = navActions::goBack)

            //Create event
            createEventScreen(onBack = navActions::goBack)

            //Event details
            eventDetailsScreen(
                onBack = navActions::goBack,
                onNavigateToEventMap = navActions::toEventMap,
                onNavigateToEditEvent = navActions::toEditEvent,
            )

            //Edit event
            editEventScreen(onBack = navActions::goBack)

            //Duplicate event
            duplicateEventScreen(onBack = navActions::goBack)

            //Event map
            eventMapScreen(onBack = navActions::goBack)

            //Favorites
            favoriteActivitiesScreen(
                onBack = navActions::goBack,
                onNavigateToEventDetails = navActions::toEventDetails
            )
        }
    }
}