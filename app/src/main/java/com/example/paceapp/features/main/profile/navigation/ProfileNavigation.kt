package com.example.paceapp.features.main.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.main.profile.ProfileScreen

@Serializable
data object ProfileRoute

fun NavGraphBuilder.profileScreen(
    onBack: () -> Unit
) {
    composable<ProfileRoute> {
        ProfileScreen(
            onBack = onBack
        )
    }
}
