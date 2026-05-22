package net.paceapp.features.main.analyticsdetail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.features.main.analyticsdetail.AnalyticsDetailScreen
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
