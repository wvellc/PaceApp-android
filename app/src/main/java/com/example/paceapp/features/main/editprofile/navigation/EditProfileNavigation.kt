package com.example.paceapp.features.main.editprofile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.main.editprofile.EditProfileScreen

@Serializable
data object EditProfileRoute

fun NavGraphBuilder.editprofileScreen(
    onBack: () -> Unit
) {
    composable<EditProfileRoute> {
        EditProfileScreen(
            onBack = onBack
        )
    }
}
