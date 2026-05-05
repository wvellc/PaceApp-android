package com.example.paceapp.features.authentication.buildprofile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.features.authentication.buildprofile.BuildProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object BuildProfileRoute

fun NavGraphBuilder.buildProfileScreen(
    onBack: () -> Unit,
    onNavigateToProfileCreated: () -> Unit
) {
    composable<BuildProfileRoute> {
        BuildProfileScreen(
            onBack = onBack,
            onNavigateToProfileCreated = onNavigateToProfileCreated,
        )
    }
}
