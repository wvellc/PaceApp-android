package net.paceapp.features.main.eventdetails.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.eventdetails.EventDetailsScreen

@Serializable
data object EventDetailsRoute

fun NavGraphBuilder.eventDetailsScreen(
    onBack: () -> Unit
) {
    composable<EventDetailsRoute> {
        EventDetailsScreen(
            onBack = onBack
        )
    }
}
