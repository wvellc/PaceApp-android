package com.example.paceapp.features.main.analytics.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.example.paceapp.features.main.analytics.AnalyticsScreen

@Serializable
data object AnalyticsRoute

fun NavGraphBuilder.analyticsScreen(
    onBack: () -> Unit
) {
    composable<AnalyticsRoute> {
        AnalyticsScreen(
            onBack = onBack
        )
    }
}
