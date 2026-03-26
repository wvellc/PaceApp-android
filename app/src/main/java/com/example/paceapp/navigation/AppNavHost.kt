package com.example.paceapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Route = Route.Splash // Always start at Splash!
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
//        composable<Route.Splash> {
//            // TODO: Replace with features.splash.SplashScreen
//            PlaceholderScreen("Splash Screen\n(Checking Session...)")
//        }
//
//        composable<Route.Auth> {
//            // TODO: Replace with features.auth.LoginScreen
//            PlaceholderScreen("Login Screen")
//        }
//
//        composable<Route.Main> {
//            // TODO: Replace with features.main.MainScreen
//            PlaceholderScreen("Main Tabs Wrapper")
//        }
//
//        composable<Route.Offline> {
//            // TODO: Replace with features.offline.NoInternetScreen
//            PlaceholderScreen("No Internet Connection")
//        }
    }
}

// A temporary UI so the app actually builds and runs
@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}