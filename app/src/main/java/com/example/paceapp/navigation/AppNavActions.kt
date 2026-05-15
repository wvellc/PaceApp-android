package com.example.paceapp.navigation

import androidx.navigation.NavHostController
import com.example.paceapp.core.domain.enums.LoginTypes
import com.example.paceapp.features.authentication.buildprofile.navigation.BuildProfileRoute
import com.example.paceapp.features.authentication.login.navigation.LoginRoute
import com.example.paceapp.features.authentication.profilecreated.navigation.ProfileCreatedRoute
import com.example.paceapp.features.authentication.verifyotp.navigation.VerifyOtpRoute
import com.example.paceapp.features.common.webview.navigation.WebviewRoute
import com.example.paceapp.core.enums.AnalyticsMetricType
import com.example.paceapp.features.main.analyticsdetail.navigation.AnalyticsDetailRoute
import com.example.paceapp.features.main.settings.navigation.SettingsRoute
import com.example.paceapp.features.main.tabhost.navigation.TabHostRoute

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

    /** Feature removed */
//     fun toOtpSuccess(loginType: LoginTypes) {
//        navController.navigate(OtpSuccessRoute(loginType = loginType)) {
//            popUpTo(navController.graph.id) { inclusive = true }
//            launchSingleTop = true
//        }
//    }

    fun toBuildProfile() {
        navController.navigate(BuildProfileRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun toProfileCreated() {
        navController.navigate(ProfileCreatedRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun toTabHost() {
        navController.navigate(TabHostRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun toSettings() {
        navController.navigate(SettingsRoute) {
            launchSingleTop = true
        }
    }

    fun toAnalyticsDetails(type: AnalyticsMetricType) {
        navController.navigate(AnalyticsDetailRoute(type = type)) {
            launchSingleTop = true
        }
    }
}