package net.paceapp.features.main.strava.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.strava.StravaConnectScreen

@Serializable
data object StravaConnectRoute

fun NavGraphBuilder.stravaConnectScreen(
    onBack: () -> Unit,
) {
    composable<StravaConnectRoute> {
        StravaConnectScreen(
            onBack = onBack,
        )
    }
}
