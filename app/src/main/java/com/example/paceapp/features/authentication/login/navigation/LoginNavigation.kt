package com.example.paceapp.features.authentication.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.authentication.login.LoginScreen

@Serializable
data object LoginRoute

fun NavGraphBuilder.loginScreen(
    // TODO: Add navigation callbacks here (e.g., onNavigateBack: () -> Unit)
) {
    composable<LoginRoute> {
        LoginScreen()
    }
}
