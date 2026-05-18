package com.example.paceapp.features.main.updategait.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.main.updategait.UpdateGaitScreen

@Serializable
data object UpdateGaitRoute

fun NavGraphBuilder.updateGaitScreen(
    onBack: () -> Unit
) {
    composable<UpdateGaitRoute> {
        UpdateGaitScreen(
            onBack = onBack
        )
    }
}
