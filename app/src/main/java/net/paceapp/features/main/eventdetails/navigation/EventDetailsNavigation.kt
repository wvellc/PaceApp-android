package net.paceapp.features.main.eventdetails.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.eventdetails.EventDetailsScreen

@Serializable
data class EventDetailsRoute(
    val id: String,
    val eventName: String,
    val location: String,
    val date: String,
)

fun NavGraphBuilder.eventDetailsScreen(
    onBack: () -> Unit,
    onNavigateToEventMap: () -> Unit,
    onNavigateToEditEvent: (String, String, String) -> Unit,
) {
    composable<EventDetailsRoute> {
        EventDetailsScreen(
            onBack = onBack,
            onNavigateToEventMap = onNavigateToEventMap,
            onNavigateToEditEvent = onNavigateToEditEvent
        )
    }
}
