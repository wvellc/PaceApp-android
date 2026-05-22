package net.paceapp.features.main.analytics.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.features.main.analytics.AnalyticsScreen
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
