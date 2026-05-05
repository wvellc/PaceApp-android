package com.example.paceapp.features.main.tabhost.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.main.tabhost.TabHostScreen

@Serializable
data object TabHostRoute
// The routes for the inner tabs
@Serializable data object HistoryRoute
@Serializable data object AnalyticsRoute
@Serializable data object ProfileRoute

fun NavGraphBuilder.tabHostScreen(
    onBack: () -> Unit
) {
    composable<TabHostRoute> {
        TabHostScreen(
            onBack = onBack
        )
    }
}
