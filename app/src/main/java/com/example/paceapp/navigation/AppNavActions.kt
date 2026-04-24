package com.example.paceapp.navigation

import androidx.navigation.NavHostController
import com.example.paceapp.features.authentication.data.enums.LoginTypes
import com.example.paceapp.features.authentication.login.navigation.LoginRoute
import com.example.paceapp.features.authentication.verifyotp.navigation.VerifyOtpRoute
import com.example.paceapp.features.common.webview.navigation.WebviewRoute

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


    fun toWebView(url: String, title: String? = null, isZoomEnabled: Boolean = false) {
        navController.navigate(
            WebviewRoute(
                url = url,
                title = title,
                isZoomEnabled = isZoomEnabled
            )
        ) {
            launchSingleTop = true
        }
    }

    fun toVerifyOtp(loginType: LoginTypes, value: String, countryCode: String? = null) {
        navController.navigate(
            VerifyOtpRoute(
                loginType = loginType,
                emailPhoneValue = value,
                countryCode = countryCode
            )
        ) {
            launchSingleTop = true
        }
    }

    fun toDashboard() {
//        navController.navigate(DashboardRoute) {
//            popUpTo(navController.graph.id) { inclusive = true }
//            launchSingleTop = true
//        }
    }
}