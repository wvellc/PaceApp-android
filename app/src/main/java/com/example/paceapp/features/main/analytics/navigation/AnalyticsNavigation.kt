package com.example.paceapp.features.main.analytics.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.core.enums.AnalyticsMetricType
import com.example.paceapp.features.main.analytics.AnalyticsScreen
import kotlinx.serialization.Serializable

@Serializable
data object AnalyticsRoute

fun NavGraphBuilder.analyticsScreen(
    onBack: () -> Unit,
    onNavigateToAnalyticsDetails: (AnalyticsMetricType) -> Unit
) {
    composable<AnalyticsRoute> {
        AnalyticsScreen(
            onBack = onBack,
            onNavigateToAnalyticsDetails = onNavigateToAnalyticsDetails
        )
    }
}
