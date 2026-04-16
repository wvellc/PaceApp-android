package com.example.paceapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.paceapp.features.authentication.login.navigation.loginScreen
import com.example.paceapp.features.splash.navigation.SplashRoute
import com.example.paceapp.features.splash.navigation.splashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Any = SplashRoute // Always start at Splash!
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        splashScreen()
        loginScreen()


    }
}