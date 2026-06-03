package net.paceapp.features.main.favoriteactivities.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesScreen

@Serializable
data object FavoriteActivitiesRoute

fun NavGraphBuilder.favoriteActivitiesScreen(
    onBack: () -> Unit,
    onNavigateToEventDetails: (String, String, String, String) -> Unit
) {
    composable<FavoriteActivitiesRoute> {
        FavoriteActivitiesScreen(
            onBack = onBack,
            onNavigateToEventDetails = onNavigateToEventDetails
        )
    }
}
