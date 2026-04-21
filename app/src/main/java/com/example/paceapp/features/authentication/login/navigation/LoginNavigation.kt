package com.example.paceapp.features.authentication.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.features.authentication.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

fun NavGraphBuilder.loginScreen(
    onBack: () -> Unit,
    onNavigateToWebview: (url: String, title: String?) -> Unit,
) {
    composable<LoginRoute> {
        LoginScreen(
            onBack = onBack,
            onNavigateToWebview = onNavigateToWebview
        )
    }
}
