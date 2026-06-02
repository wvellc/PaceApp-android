package net.paceapp.features.main.editevent.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.editevent.EditEventScreen

@Serializable
data class EditEventRoute(
    val id: String,
    val eventName: String,
    val location: String,
)

fun NavGraphBuilder.editEventScreen(
    onBack: () -> Unit
) {
    composable<EditEventRoute> {
        EditEventScreen(
            onBack = onBack
        )
    }
}
