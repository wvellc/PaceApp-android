package net.paceapp.features.main.eventmap.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.eventmap.EventMapScreen

@Serializable
data object EventMapRoute

fun NavGraphBuilder.eventMapScreen(
    onBack: () -> Unit
) {
    composable<EventMapRoute> {
        EventMapScreen(
            onBack = onBack
        )
    }
}
