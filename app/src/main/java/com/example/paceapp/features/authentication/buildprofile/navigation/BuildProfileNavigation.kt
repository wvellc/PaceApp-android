package com.example.paceapp.features.authentication.buildprofile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.authentication.buildprofile.BuildProfileScreen

@Serializable
data object BuildProfileRoute

fun NavGraphBuilder.buildProfileScreen(
    onBack: () -> Unit
) {
    composable<BuildProfileRoute> {
        BuildProfileScreen(
            onBack = onBack
        )
    }
}
