package net.paceapp.features.main.createevent.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kyant.backdrop.backdrops.LayerBackdrop
import kotlinx.serialization.Serializable
import net.paceapp.features.main.createevent.CreateEventScreen

@Serializable
data object CreateEventRoute

fun NavGraphBuilder.createEventScreen(
    onBack: () -> Unit,
) {
    composable<CreateEventRoute> {
        CreateEventScreen(
            onBack = onBack
        )
    }
}
