package net.paceapp.features.main.duplicateevent.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.duplicateevent.DuplicateEventScreen

@Serializable
data class DuplicateEventRoute(
    val id: String,
    val eventName: String,
    val location: String,
    val date: String
)

fun NavGraphBuilder.duplicateEventScreen(
    onBack: () -> Unit
) {
    composable<DuplicateEventRoute> {
        DuplicateEventScreen(
            onBack = onBack
        )
    }
}
