package com.example.paceapp.features.main.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.features.main.profile.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

fun NavGraphBuilder.profileScreen(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSetGait: () -> Unit,

    ) {
    composable<ProfileRoute> {
        ProfileScreen(
            onBack = onBack,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToSetGait = onNavigateToSetGait,
        )
    }
}
