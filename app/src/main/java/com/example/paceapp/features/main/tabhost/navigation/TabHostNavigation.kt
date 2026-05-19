package com.example.paceapp.features.main.tabhost.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.core.enums.AnalyticsMetricType
import com.example.paceapp.features.main.tabhost.TabHostScreen
import kotlinx.serialization.Serializable

@Serializable
data object TabHostRoute

fun NavGraphBuilder.tabHostScreen(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnalyticsDetails: (AnalyticsMetricType) -> Unit,
    onNavigateToSetGait: () -> Unit,
    onNavigateToEditProfile: () -> Unit
) {
    composable<TabHostRoute> {
        TabHostScreen(
            onBack = onBack,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToAnalyticsDetails = onNavigateToAnalyticsDetails,
            onNavigateToSetGait = onNavigateToSetGait,
            onNavigateToEditProfile = onNavigateToEditProfile,
        )
    }
}
