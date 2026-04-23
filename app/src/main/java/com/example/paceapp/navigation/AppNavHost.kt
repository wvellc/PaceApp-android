package com.example.paceapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.paceapp.features.authentication.login.navigation.loginScreen
import com.example.paceapp.features.authentication.verifyotp.navigation.verifyOtpScreen
import com.example.paceapp.features.common.webview.navigation.webviewScreen
import com.example.paceapp.features.splash.navigation.SplashRoute
import com.example.paceapp.features.splash.navigation.splashScreen
import com.wvelabs.core_ui.utils.rememberGlobalExitHandler

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Any = SplashRoute,
) {
    //Nav action
    val triggerExit = rememberGlobalExitHandler()
    val navActions = remember(navController) {
        AppNavActions(navController, onRootExit = triggerExit)
    }
    // Navigation transition animation and duration
    val durationMillis = 400
    val defaultEasing = FastOutSlowInEasing

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
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
            onNavigateToDashboard = navActions::toDashboard,
        )

        //Login screen
        loginScreen(
            onBack = navActions::goBack,
            onNavigateToWebview = navActions::toWebView
        )

        //Webview
        webviewScreen()

        //Verify Otp
        verifyOtpScreen()

    }
}