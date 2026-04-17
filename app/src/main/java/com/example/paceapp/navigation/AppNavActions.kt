package com.example.paceapp.navigation

import androidx.navigation.NavHostController
import com.example.paceapp.features.authentication.login.navigation.LoginRoute

class AppNavActions(
    private val navController: NavHostController,
    private val onRootExit: () -> Unit
) {

    fun goBack() {
        when {
            navController.previousBackStackEntry != null -> navController.popBackStack()
            else -> onRootExit()
        }
    }

    fun toLogin() {
        navController.navigate(LoginRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun toDashboard() {
//        navController.navigate(DashboardRoute) {
//            popUpTo(navController.graph.id) { inclusive = true }
//            launchSingleTop = true
//        }
    }
//
//    // Look how clean passing arguments is now! No Uri.encode needed!
//    fun toWebView(url: String, title: String) {
//        navController.navigate(WebViewRoute(url = url, title = title)) {
//            launchSingleTop = true
//        }
//    }
}