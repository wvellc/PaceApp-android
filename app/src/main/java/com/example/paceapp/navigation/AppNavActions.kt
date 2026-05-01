package com.example.paceapp.navigation

import androidx.navigation.NavHostController
import com.example.paceapp.features.authentication.buildprofile.navigation.BuildProfileRoute
import com.example.paceapp.core.domain.enums.LoginTypes
import com.example.paceapp.features.authentication.login.navigation.LoginRoute
import com.example.paceapp.features.authentication.otpsuccess.navigation.OtpSuccessRoute
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

    fun toOtpSuccess(loginType: LoginTypes) {
        navController.navigate(OtpSuccessRoute(loginType = loginType)) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun toBuildProfile() {
        navController.navigate(BuildProfileRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }


    fun toTabHost() {
//        navController.navigate(TabHostRoute) {
//            popUpTo(navController.graph.id) { inclusive = true }
//            launchSingleTop = true
//        }
    }
}