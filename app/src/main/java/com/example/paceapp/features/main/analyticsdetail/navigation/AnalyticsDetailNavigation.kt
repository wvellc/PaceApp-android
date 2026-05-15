package com.example.paceapp.features.main.analyticsdetail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.core.enums.AnalyticsMetricType
import com.example.paceapp.features.main.analyticsdetail.AnalyticsDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsDetailRoute(val type: AnalyticsMetricType)

fun NavGraphBuilder.analyticsDetailScreen(
    onBack: () -> Unit
) {
    composable<AnalyticsDetailRoute> {
        AnalyticsDetailScreen(
            onBack = onBack
        )
    }
}
