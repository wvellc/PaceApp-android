package net.paceapp.features.authentication.profilecreated.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import net.paceapp.features.authentication.profilecreated.ProfileCreatedScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProfileCreatedRoute

fun NavGraphBuilder.profileCreatedScreen(
    onBack: () -> Unit,
    onNavigateToTabHost: () -> Unit
) {
    composable<ProfileCreatedRoute> {
        ProfileCreatedScreen(
            onBack = onBack,
            onNavigateToTabHost = onNavigateToTabHost
        )
    }
}
