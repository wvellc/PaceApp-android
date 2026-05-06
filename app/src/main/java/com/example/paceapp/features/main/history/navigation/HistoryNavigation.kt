package com.example.paceapp.features.main.history.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.main.history.HistoryScreen

@Serializable
data object HistoryRoute

fun NavGraphBuilder.historyScreen(
    onBack: () -> Unit
) {
    composable<HistoryRoute> {
        HistoryScreen(
            onBack = onBack
        )
    }
}
